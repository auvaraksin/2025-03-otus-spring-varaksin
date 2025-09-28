package ru.otus.projectwork.user.service.chain.authorization.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.domain.model.projection.UserAuthorizationProjection;
import ru.otus.projectwork.user.dto.ChainAuthorizationDto;
import ru.otus.projectwork.user.exception.NotFoundException;
import ru.otus.projectwork.user.service.chain.authorization.ProcessAuthorizationChainPart;

import static ru.otus.projectwork.user.service.chain.authorization.impl.CheckClientPasswordChainPart.ORDER;
import static ru.otus.projectwork.user.util.ExceptionMessage.ACCOUNT_DOES_NOT_EXISTS;
import static ru.otus.projectwork.user.util.ExceptionMessage.INCORRECT_PASSWORD;

/**
 * Часть цепочки для проверки пароля клиента.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class CheckClientPasswordChainPart implements ProcessAuthorizationChainPart {

    protected static final int ORDER = 3;

    public static final String SPACE = "";

    private final PasswordEncoder passwordEncoder;

    /**
     * Выполняет проверку введенного пароля клиента и полученного из базы данных. В случае несоответствия выбрасывается
     * исключение BadRequestException, в случае соответствия происходит переход на новый этап авторизации.
     *
     * @param chain объект ChainAuthorizationDto, содержащий данные для обработки
     * @return ChainAuthorizationDto после успешной проверки пароля клиента
     * @throws ru.otus.projectwork.user.exception.NotFoundException в случае несоответствия введенного пароля с паролем из базы данных
     */
    @Override
    public ChainAuthorizationDto process(ChainAuthorizationDto chain) {

        String rawPassword = chain.getRequest().password();

        UserAuthorizationProjection userInfo = chain.getUserInfo()
                .orElseThrow(() -> new NotFoundException(ACCOUNT_DOES_NOT_EXISTS.getDescription()));

        if (!passwordEncoder.matches(rawPassword, userInfo.getPassword())) {
            throw new NotFoundException(INCORRECT_PASSWORD.getDescription());
        }

        chain.setClientId(userInfo.getId());
        chain.setUserFullName(String.join(SPACE, userInfo.getFirstName(), userInfo.getLastName()));

        return chain;
    }
}
