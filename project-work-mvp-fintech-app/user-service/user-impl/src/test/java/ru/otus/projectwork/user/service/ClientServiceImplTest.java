package ru.otus.projectwork.user.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import ru.otus.projectwork.domain.model.Address;
import ru.otus.projectwork.domain.model.Client;
import ru.otus.projectwork.domain.model.PassportData;
import ru.otus.projectwork.domain.model.UserProfile;
import ru.otus.projectwork.domain.model.projection.UserAuthorizationProjection;
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
import ru.otus.projectwork.user.service.chain.authorization.ProcessAuthorizationChainPart;
import ru.otus.projectwork.user.service.chain.registration.ProcessRegistrationChainPart;
import ru.otus.projectwork.user.service.impl.ClientServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.otus.projectwork.user.util.ExceptionMessage.CLIENT_BY_MOBILE_PHONE_NOT_FOUND;
import static ru.otus.projectwork.user.util.ExceptionMessage.CLIENT_WITH_PASSPORT_NUMBER_EXIST;
import static ru.otus.projectwork.user.util.ExceptionMessage.INCORRECT_CODE_ENTERED;
import static ru.otus.projectwork.user.util.ExceptionMessage.MANY_REQUEST;
import static ru.otus.projectwork.user.util.ExceptionMessage.MOBILE_PHONE_REGISTERED;
import static ru.otus.projectwork.user.util.ExceptionMessage.NO_RECORD_IN_THE_SYSTEM;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование ClientServiceImpl")
class ClientServiceImplTest {

    private List<ProcessRegistrationChainPart> registrationChainParts = new ArrayList<>();

    private List<ProcessAuthorizationChainPart> authorizationChainParts = new ArrayList<>();

    @Mock
    private PassportDataService passportDataService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private RedisConfig redisConfig;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private ClientServiceImpl clientService;

    private final String TEST_PHONE = "79991234567";
    private final String TEST_PASSPORT = "1234567890";
    private final String TEST_OTP = "123456";
    private final UUID TEST_CLIENT_ID = UUID.randomUUID();
    private final Long TEST_OTP_TTL = 120L;
    private final Integer TEST_MAX_ATTEMPTS = 3;

    @BeforeEach
    void setUp() {
        registrationChainParts = new ArrayList<>();
        authorizationChainParts = new ArrayList<>();

        clientService = new ClientServiceImpl(
                registrationChainParts,
                authorizationChainParts,
                passportDataService,
                clientRepository,
                redisTemplate,
                jwtConfig,
                redisConfig
        );
    }

    @Test
    @DisplayName("Успешная регистрация клиента")
    void registration_Success() {
        // Given
        RegistrationRequestDto request = createRegistrationRequest();

        ChainRegistrationDto finalDto = createChainRegistrationDto();

        when(passportDataService.existsByPassportNumber(TEST_PASSPORT)).thenReturn(false);

        for (int i = 0; i < 5; i++) {
            ProcessRegistrationChainPart mockPart = mock(ProcessRegistrationChainPart.class);
            if (i == 0) {
                when(mockPart.process(any(RegistrationRequestDto.class), any(ChainRegistrationDto.class)))
                        .thenReturn(finalDto);
            } else {
                when(mockPart.process(any(RegistrationRequestDto.class), any(ChainRegistrationDto.class)))
                        .thenReturn(finalDto);
            }
            registrationChainParts.add(mockPart);
        }

        // When
        RegistrationResponseDto result = clientService.registration(request);

        // Then
        assertNotNull(result);
        assertEquals(TEST_CLIENT_ID, result.id());
        verify(passportDataService).existsByPassportNumber(TEST_PASSPORT);

        for (ProcessRegistrationChainPart part : registrationChainParts) {
            verify(part).process(any(RegistrationRequestDto.class), any(ChainRegistrationDto.class));
        }
    }

    @Test
    @DisplayName("Регистрация с существующим номером паспорта - конфликт")
    void registration_WithExistingPassport_ThrowsConflictException() {
        // Given
        RegistrationRequestDto request = createRegistrationRequest();

        when(passportDataService.existsByPassportNumber(TEST_PASSPORT)).thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> clientService.registration(request));

        assertEquals(CLIENT_WITH_PASSPORT_NUMBER_EXIST.getDescription(), exception.getMessage());
        verify(passportDataService).existsByPassportNumber(TEST_PASSPORT);
        // ИСПРАВЛЕНИЕ: убираем verifyNoInteractions для списков, так как они теперь реальные объекты
    }

    @Test
    @DisplayName("Успешная проверка регистрации - номер телефона свободен")
    void checkRegistration_Success() {
        // Given
        CheckRegistrationRequestDto request = new CheckRegistrationRequestDto(TEST_PHONE);

        when(clientRepository.isMobilePhoneExists(TEST_PHONE)).thenReturn(false);

        // When
        CheckPhoneResponseDto result = clientService.checkRegistration(request);

        // Then
        assertNotNull(result);
        assertEquals(TEST_PHONE, result.mobilePhone());
        verify(clientRepository).isMobilePhoneExists(TEST_PHONE);
    }

    @Test
    @DisplayName("Проверка регистрации с занятым номером телефона - конфликт")
    void checkRegistration_WithExistingPhone_ThrowsConflictException() {
        // Given
        CheckRegistrationRequestDto request = new CheckRegistrationRequestDto(TEST_PHONE);

        when(clientRepository.isMobilePhoneExists(TEST_PHONE)).thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> clientService.checkRegistration(request));

        assertEquals(MOBILE_PHONE_REGISTERED.getDescription(), exception.getMessage());
        verify(clientRepository).isMobilePhoneExists(TEST_PHONE);
    }

    @Test
    @DisplayName("Успешная авторизация клиента")
    void authorize_Success() {
        // Given
        AuthorizationRequestDto request = createAuthorizationRequest();
        ChainAuthorizationDto chainDto = createChainAuthorizationDto();

        for (int i = 0; i < 4; i++) {
            ProcessAuthorizationChainPart mockPart = mock(ProcessAuthorizationChainPart.class);
            when(mockPart.process(any(ChainAuthorizationDto.class)))
                    .thenReturn(chainDto);
            authorizationChainParts.add(mockPart);
        }

        JwtConfig.Cookies cookiesConfig = new JwtConfig.Cookies();
        cookiesConfig.setName("auth-token");
        when(jwtConfig.getCookies()).thenReturn(cookiesConfig);

        // When
        AuthorizationResponseDto result = clientService.authorize(request, response);

        // Then
        assertNotNull(result);
        assertEquals(TEST_CLIENT_ID.toString(), result.clientId());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals("access-token", result.accessToken());

        verify(response).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("auth-token", capturedCookie.getName());
        assertEquals("access-token", capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());

        for (ProcessAuthorizationChainPart part : authorizationChainParts) {
            verify(part).process(any(ChainAuthorizationDto.class));
        }
    }

    @Test
    @DisplayName("Успешное сохранение OTP кода для существующего клиента")
    void saveOtpCode_Success() {
        // Given
        CheckRegistrationRequestDto request = new CheckRegistrationRequestDto(TEST_PHONE);

        when(clientRepository.isMobilePhoneExists(TEST_PHONE)).thenReturn(true);
        when(redisConfig.getOtpTtl()).thenReturn(TEST_OTP_TTL);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        CheckPhoneResponseDto result = clientService.saveOtpCode(request);

        // Then
        assertNotNull(result);
        assertEquals(TEST_PHONE, result.mobilePhone());
        verify(clientRepository).isMobilePhoneExists(TEST_PHONE);
        verify(valueOperations).set(eq(TEST_PHONE), anyString(), eq(TEST_OTP_TTL), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Сохранение OTP для несуществующего номера телефона - NotFoundException")
    void saveOtpCode_WithNonExistingPhone_ThrowsNotFoundException() {
        // Given
        CheckRegistrationRequestDto request = new CheckRegistrationRequestDto(TEST_PHONE);

        when(clientRepository.isMobilePhoneExists(TEST_PHONE)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> clientService.saveOtpCode(request));

        assertEquals(CLIENT_BY_MOBILE_PHONE_NOT_FOUND.getDescription().formatted(TEST_PHONE),
                exception.getMessage());
        verify(clientRepository).isMobilePhoneExists(TEST_PHONE);
        verifyNoInteractions(valueOperations);
    }

    @Test
    @DisplayName("Успешная проверка OTP кода")
    void checkOtpCode_Success() {
        // Given
        CheckOtpRequestDto request = new CheckOtpRequestDto(TEST_PHONE, TEST_OTP);
        String redisData = TEST_OTP + ",0";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_PHONE)).thenReturn(redisData);
        when(redisConfig.getMaxAttemptsToCheckOtp()).thenReturn(TEST_MAX_ATTEMPTS);

        // When
        CheckPhoneResponseDto result = clientService.checkOtpCode(request);

        // Then
        assertNotNull(result);
        assertEquals(TEST_PHONE, result.mobilePhone());
        verify(valueOperations).get(TEST_PHONE);
        verify(redisTemplate).delete(TEST_PHONE);
    }

    @Test
    @DisplayName("Проверка OTP с неверным кодом - ConflictException")
    void checkOtpCode_WithWrongCode_ThrowsConflictException() {
        // Given
        CheckOtpRequestDto request = new CheckOtpRequestDto(TEST_PHONE, "wrong-code");
        String redisData = TEST_OTP + ",0";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_PHONE)).thenReturn(redisData);
        when(redisTemplate.getExpire(TEST_PHONE, TimeUnit.SECONDS)).thenReturn(100L);
        when(redisConfig.getMaxAttemptsToCheckOtp()).thenReturn(TEST_MAX_ATTEMPTS);

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> clientService.checkOtpCode(request));

        assertEquals(INCORRECT_CODE_ENTERED.getDescription(), exception.getMessage());
        verify(valueOperations).get(TEST_PHONE);
        verify(valueOperations).set(eq(TEST_PHONE), eq(TEST_OTP + ",1"), eq(100L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Проверка OTP с превышением попыток - TooManyRequestException")
    void checkOtpCode_WithMaxAttempts_ThrowsTooManyRequestException() {
        // Given
        CheckOtpRequestDto request = new CheckOtpRequestDto(TEST_PHONE, TEST_OTP);
        String redisData = TEST_OTP + "," + TEST_MAX_ATTEMPTS;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_PHONE)).thenReturn(redisData);
        when(redisConfig.getMaxAttemptsToCheckOtp()).thenReturn(TEST_MAX_ATTEMPTS);

        // When & Then
        TooManyRequestException exception = assertThrows(TooManyRequestException.class,
                () -> clientService.checkOtpCode(request));

        assertEquals(MANY_REQUEST.getDescription(), exception.getMessage());
        verify(valueOperations).get(TEST_PHONE);
        verifyNoMoreInteractions(valueOperations);
    }

    @Test
    @DisplayName("Проверка OTP с отсутствующей записью в Redis - NotFoundException")
    void checkOtpCode_WithNoRedisData_ThrowsNotFoundException() {
        // Given
        CheckOtpRequestDto request = new CheckOtpRequestDto(TEST_PHONE, TEST_OTP);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_PHONE)).thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> clientService.checkOtpCode(request));

        assertEquals(NO_RECORD_IN_THE_SYSTEM.getDescription(), exception.getMessage());
        verify(valueOperations).get(TEST_PHONE);
    }

    @Test
    @DisplayName("Интеграционный тест: полный цикл OTP с превышением попыток")
    void checkOtpCode_Integration_ExceededAttempts() {
        // Given
        CheckOtpRequestDto firstAttempt = new CheckOtpRequestDto(TEST_PHONE, "wrong1");
        CheckOtpRequestDto secondAttempt = new CheckOtpRequestDto(TEST_PHONE, "wrong2");
        CheckOtpRequestDto thirdAttempt = new CheckOtpRequestDto(TEST_PHONE, "wrong3");

        String initialRedisData = TEST_OTP + ",0";
        String afterFirstAttempt = TEST_OTP + ",1";
        String afterSecondAttempt = TEST_OTP + ",2";
        String afterThirdAttempt = TEST_OTP + "," + TEST_MAX_ATTEMPTS;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_PHONE))
                .thenReturn(initialRedisData)    // Первая попытка
                .thenReturn(afterFirstAttempt)   // Вторая попытка
                .thenReturn(afterSecondAttempt)  // Третья попытка
                .thenReturn(afterThirdAttempt);  // Четвертая попытка (превышение)

        when(redisTemplate.getExpire(TEST_PHONE, TimeUnit.SECONDS)).thenReturn(100L);
        when(redisConfig.getMaxAttemptsToCheckOtp()).thenReturn(TEST_MAX_ATTEMPTS);

        // When & Then
        assertThrows(ConflictException.class, () -> clientService.checkOtpCode(firstAttempt));
        assertThrows(ConflictException.class, () -> clientService.checkOtpCode(secondAttempt));
        assertThrows(ConflictException.class, () -> clientService.checkOtpCode(thirdAttempt));

        // Четвертая попытка должна выбрасывать TooManyRequestException
        TooManyRequestException exception = assertThrows(TooManyRequestException.class,
                () -> clientService.checkOtpCode(thirdAttempt));

        assertEquals(MANY_REQUEST.getDescription(), exception.getMessage());

        verify(valueOperations, times(3)).set(eq(TEST_PHONE), anyString(), eq(100L), eq(TimeUnit.SECONDS));
    }

    // Вспомогательные методы для создания тестовых данных
    private RegistrationRequestDto createRegistrationRequest() {
        return RegistrationRequestDto.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .mobilePhone(TEST_PHONE)
                .passportNumber(TEST_PASSPORT)
                .issuedBy("ОВД района")
                .issueDate(LocalDate.now().minusYears(5))
                .departmentCode("123456")
                .birthDate(LocalDate.now().minusYears(20))
                .country("Россия")
                .city("Москва")
                .street("Ленина")
                .house("10")
                .hull("1")
                .flat("25")
                .postCode("123456")
                .password("Password123!")
                .email("ivan@example.com")
                .build();
    }

    private AuthorizationRequestDto createAuthorizationRequest() {
        return AuthorizationRequestDto.builder()
                .mobilePhone(TEST_PHONE)
                .passportNumber(TEST_PASSPORT)
                .password("Password123!")
                .build();
    }

    private ChainRegistrationDto createChainRegistrationDto() {
        Client client = Client.builder()
                .id(TEST_CLIENT_ID)
                .firstName("Иван")
                .lastName("Иванов")
                .mobilePhone(TEST_PHONE)
                .build();

        return ChainRegistrationDto.builder()
                .client(client)
                .passportData(mock(PassportData.class))
                .addressRegistration(mock(Address.class))
                .addressActual(mock(Address.class))
                .userProfile(mock(UserProfile.class))
                .build();
    }

    private ChainAuthorizationDto createChainAuthorizationDto() {
        UserAuthorizationProjection userAuthProjection = mock(UserAuthorizationProjection.class);
        lenient().when(userAuthProjection.getId()).thenReturn(TEST_CLIENT_ID);
        lenient().when(userAuthProjection.getFirstName()).thenReturn("Иван");
        lenient().when(userAuthProjection.getLastName()).thenReturn("Иванов");
        lenient().when(userAuthProjection.getPassword()).thenReturn("encodedPassword");

        return ChainAuthorizationDto.builder()
                .clientId(TEST_CLIENT_ID)
                .userFullName("Иван Иванов")
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .userInfo(Optional.of(userAuthProjection))
                .build();
    }
}
