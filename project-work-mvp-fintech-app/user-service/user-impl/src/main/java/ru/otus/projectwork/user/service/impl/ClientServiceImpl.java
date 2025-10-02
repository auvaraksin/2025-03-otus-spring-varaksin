package ru.otus.projectwork.user.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.projectwork.domain.repository.ClientRepository;
import ru.otus.projectwork.user.configuration.JwtConfig;
import ru.otus.projectwork.user.configuration.RedisConfig;
import ru.otus.projectwork.user.dto.ChainAuthorizationDto;
import ru.otus.projectwork.user.dto.ChainRegistrationDto;
import ru.otus.projectwork.user.dto.request.AuthorizationRequestDto;
import ru.otus.projectwork.user.dto.request.CheckOtpRequestDto;
import ru.otus.projectwork.user.dto.request.CheckRegistrationRequestDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.dto.response.AuthorizationResponseDto;
import ru.otus.projectwork.user.dto.response.CheckPhoneResponseDto;
import ru.otus.projectwork.user.dto.response.RegistrationResponseDto;
import ru.otus.projectwork.user.exception.ConflictException;
import ru.otus.projectwork.user.exception.NotFoundException;
import ru.otus.projectwork.user.exception.TooManyRequestException;
import ru.otus.projectwork.user.service.ClientService;
import ru.otus.projectwork.user.service.PassportDataService;
import ru.otus.projectwork.user.service.chain.authorization.ProcessAuthorizationChainPart;
import ru.otus.projectwork.user.service.chain.registration.ProcessRegistrationChainPart;
import ru.otus.projectwork.user.util.Constant;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hibernate.type.descriptor.java.IntegerJavaType.ZERO;
import static ru.otus.projectwork.user.util.ExceptionMessage.CLIENT_BY_MOBILE_PHONE_NOT_FOUND;
import static ru.otus.projectwork.user.util.ExceptionMessage.CLIENT_WITH_PASSPORT_NUMBER_EXIST;
import static ru.otus.projectwork.user.util.ExceptionMessage.INCORRECT_CODE_ENTERED;
import static ru.otus.projectwork.user.util.ExceptionMessage.MANY_REQUEST;
import static ru.otus.projectwork.user.util.ExceptionMessage.MOBILE_PHONE_REGISTERED;
import static ru.otus.projectwork.user.util.ExceptionMessage.NO_RECORD_IN_THE_SYSTEM;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    List<ProcessRegistrationChainPart> registrationChainParts;

    List<ProcessAuthorizationChainPart> authorizationChainParts;

    PassportDataService passportDataService;

    ClientRepository clientRepository;

    RedisTemplate<String, Object> redisTemplate;

    JwtConfig JWTConfig;

    RedisConfig redisConfig;

    @Override
    @Transactional
    public RegistrationResponseDto registration(RegistrationRequestDto request) {

        if (passportDataService.existsByPassportNumber(request.passportNumber())) {
            throw new ConflictException(CLIENT_WITH_PASSPORT_NUMBER_EXIST.getDescription());
        }

        ChainRegistrationDto chainDto = ChainRegistrationDto.builder().build();

        for (ProcessRegistrationChainPart part : registrationChainParts) {
            chainDto = part.process(request, chainDto);
        }

        return RegistrationResponseDto.builder()
                .id(chainDto.getClient().getId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CheckPhoneResponseDto checkRegistration(CheckRegistrationRequestDto request) {

        String phoneNumber = request.mobilePhone();
        if (clientRepository.isMobilePhoneExists(phoneNumber)) {
            throw new ConflictException(MOBILE_PHONE_REGISTERED.getDescription());
        }

        return new CheckPhoneResponseDto(phoneNumber);
    }

    @Override
    @Transactional
    public AuthorizationResponseDto authorize(AuthorizationRequestDto request, HttpServletResponse response) {

        ChainAuthorizationDto chainDto = ChainAuthorizationDto
                .builder()
                .request(request)
                .build();

        for (ProcessAuthorizationChainPart part : authorizationChainParts) {
            chainDto = part.process(chainDto);
        }

        String jwt = chainDto.getAccessToken();
        Cookie cookie = new Cookie(JWTConfig.getCookies().getName(), jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return AuthorizationResponseDto
                .builder()
                .clientId(chainDto.getUserInfo().orElseThrow().getId().toString())
                .refreshToken(chainDto.getRefreshToken())
                .accessToken(chainDto.getAccessToken())
                .build();
    }

    @Override
    @Transactional
    public CheckPhoneResponseDto saveOtpCode(CheckRegistrationRequestDto request) {

        String phone = request.mobilePhone();

        if (!clientRepository.isMobilePhoneExists(phone)) {
            throw new NotFoundException(
                    CLIENT_BY_MOBILE_PHONE_NOT_FOUND.getDescription().formatted(phone));
        }

        return saveOtpCode(phone);
    }

    @Override
    @Transactional
    public CheckPhoneResponseDto checkOtpCode(CheckOtpRequestDto request) {
        String phone = request.mobilePhone();
        String otpFromUser = request.otpCode();

        String[] resultFromDb = getRedisData(phone).split(",");
        String otpCodeInDb = resultFromDb[0];

        int numberAttempts = Integer.parseInt(resultFromDb[1]);

        validateNumberAttempts(numberAttempts);

        if (otpFromUser.equals(otpCodeInDb)) {
            redisTemplate.delete(phone);
            return new CheckPhoneResponseDto(phone);
        } else {
            updateAttemptsInRedis(phone, otpCodeInDb, ++numberAttempts);
            throw new ConflictException(INCORRECT_CODE_ENTERED.getDescription());
        }
    }

    /**
     * Сохранение OTP кода.
     * <p>
     * Создает запись в Redis с OTP кодом и отправляет сообщение клиенту в консоль приложения (заглушка
     * реального провайдера сервиса отправки).
     * </p>
     *
     * @param phone номер телефона для сохранения в Redis в качестве ключа
     */
    private CheckPhoneResponseDto saveOtpCode(String phone) {

        String otpCode = createOtp();

        int numberAttempts = ZERO;

        String stringCodeAndAttempts = String.format("%s,%s", otpCode, numberAttempts);

        saveOtpToRedis(phone, stringCodeAndAttempts, redisConfig.getOtpTtl());

        System.out.printf(
                "OTP-код %s направлен клиенту по номеру телефона +%s. Срок действия OTP-кода %s секунд%n",
                otpCode, phone, redisConfig.getOtpTtl());
        return new CheckPhoneResponseDto(phone);
    }

    /**
     * Создание OTP кода.
     *
     * @return OTP код состоящий из 6 случайных чисел от 0 до 9.
     */
    private String createOtp() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            str.append(Constant.RANDOM.nextInt(10));
        }
        return str.toString();
    }

    /**
     * Сохранение OTP кода в Redis.
     */
    private void saveOtpToRedis(String phone, String stringCodeAndAttempts, Long expirationDate) {
        redisTemplate.opsForValue().set(phone, stringCodeAndAttempts, expirationDate, TimeUnit.SECONDS);
    }

    /**
     * Получение данных из Redis.
     *
     * @param phone ключ для получения значения из Redis.
     * @return значение по ключу phone.
     * @throws NotFoundException - если значение отсутствует в бд.
     */
    private String getRedisData(String phone) {
        Object redisData = redisTemplate.opsForValue().get(phone);

        if (redisData == null) {
            throw new NotFoundException(NO_RECORD_IN_THE_SYSTEM.getDescription());
        }

        return redisData.toString();
    }

    /**
     * Обновление количества использованных попыток и оставшегося времени жизни OTP-кода в Redis.
     *
     * @param phone ключ для обновления numberAttempts в значении Redis.
     */
    private void updateAttemptsInRedis(String phone, String otpCodeInDb, int numberAttempts) {
        String updateString = String.format("%s,%s", otpCodeInDb, numberAttempts);
        Long ttl = redisTemplate.getExpire(phone, TimeUnit.SECONDS);
        if (ttl != null && ttl > 0) {
            saveOtpToRedis(phone, updateString, ttl);
        }
    }

    /**
     * Проверка на превышение допустимого количества попыток.
     *
     * @param numberAttempts количество сделанных попыток.
     * @throws ru.otus.projectwork.user.exception.TooManyRequestException если количество запросов на проверку больше допустимого
     */
    private void validateNumberAttempts(int numberAttempts) {
        if (numberAttempts >= redisConfig.getMaxAttemptsToCheckOtp()) {
            throw new TooManyRequestException(MANY_REQUEST.getDescription());
        }
    }

}
