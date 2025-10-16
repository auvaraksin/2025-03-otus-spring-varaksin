package ru.otus.projectwork.user.service.chain.registration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.domain.model.UserProfile;
import ru.otus.projectwork.domain.model.enums.AuthorizationType;
import ru.otus.projectwork.domain.repository.UserProfileRepository;
import ru.otus.projectwork.user.dto.ChainRegistrationDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.service.chain.registration.ProcessRegistrationChainPart;

import static ru.otus.projectwork.user.service.chain.registration.impl.CreateUserProfileChainPart.ORDER;

/**
 * Часть цепочки для создания профиля пользователя.
 * <p>
 * Эта часть цепочки отвечает за создание новой записи профиля пользователя на основе данных запроса регистрации.
 * Профиль пользователя сохраняется в репозитории профилей пользователей.
 * <p>
 * Порядок выполнения: 5
 * <p>
 * Процесс работы: Этот компонент цепочки регистрации принимает данные запроса регистрации и создает новый профиль
 * пользователя. Созданный профиль пользователя сохраняется в указанном репозитории профилей пользователей.
 * <p>
 * Пример использования:
 * <pre>{@code
 * RegistrationRequestDto request = RegistrationRequestDtoUtil.getValidRegistrationRequestDto();
 * ChainRegistrationDto previous = ChainRegistrationDto.builder().build();
 *
 * ChainRegistrationDto result = createUserProfileChainPart.process(request, previous);
 * }</pre>
 * <p>
 * Примечания: Для успешного создания профиля пользователя необходимо, чтобы в запросе регистрации были заполнены
 * необходимые поля, такие как пароль и адрес электронной почты. Устанавливаются также значения по умолчанию для других
 * параметров профиля пользователя, таких как включение уведомлений и тип авторизации.
 * <p>
 * Предусловия: Входные данные должны соответствовать структуре и типам, определенным в классе RegistrationRequestDto.
 * <p>
 * Постусловия: После выполнения метода process() в объекте previous будет обновлена информация о профиле пользователя.
 * <p>
 * Исключения: Может выбрасывать исключения в случае ошибок сохранения данных в репозитории или если входные данные не
 * удовлетворяют требованиям.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class CreateUserProfileChainPart implements ProcessRegistrationChainPart {

    protected static final int ORDER = 5;

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Создает новую запись профиля пользователя на основе данных запроса регистрации. Сохраняет созданный профиль
     * пользователя в репозитории профилей пользователей.
     *
     * @param request  данные запроса регистрации
     * @param previous объект, содержащий результаты предыдущих этапов цепочки регистрации
     * @return объект ChainRegistrationDto с обновленными данными о профиле пользователя
     */
    @Override
    public ChainRegistrationDto process(RegistrationRequestDto request, ChainRegistrationDto previous) {

        UserProfile userProfile = getUserProfile(request, previous);

        userProfile = userProfileRepository.save(userProfile);

        previous.setUserProfile(userProfile);
        return previous;
    }

    /**
     * Формирует объект UserProfile на основе данных запроса регистрации и предыдущих результатов цепочки.
     *
     * @param request  данные запроса регистрации
     * @param previous объект, содержащий результаты предыдущих этапов цепочки регистрации
     * @return объект UserProfile с данными о профиле пользователя
     */
    private UserProfile getUserProfile(RegistrationRequestDto request, ChainRegistrationDto previous) {
        return UserProfile.builder()
                .client(previous.getClient())
                .password(passwordEncoder.encode(request.password()))
                .isSmsEnabled(false)
                .isPushEnabled(false)
                .email(request.email())
                .isEmailSubscription(false)
                .authorizationType(AuthorizationType.LOGIN)
                .build();
    }

}
