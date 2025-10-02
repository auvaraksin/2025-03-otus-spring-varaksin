package ru.otus.projectwork.user.service.chain.authorization.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.user.dto.ChainAuthorizationDto;
import ru.otus.projectwork.user.dto.request.AuthorizationRequestDto;
import ru.otus.projectwork.user.exception.BadRequestException;
import ru.otus.projectwork.user.service.chain.authorization.ProcessAuthorizationChainPart;

import static ru.otus.projectwork.user.service.chain.authorization.impl.CheckAuthorizationParamsChainPart.ORDER;
import static ru.otus.projectwork.user.util.ExceptionMessage.AUTHORIZATION_PARAMS_NOT_VALID;

/**
 * Часть цепочки для проверки параметров запроса на авторизацию клиента.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class CheckAuthorizationParamsChainPart implements ProcessAuthorizationChainPart {

    protected static final int ORDER = 1;

    /**
     * Выполняет проверку параметров авторизации клиента.
     *
     * @param chain объект ChainAuthorizationDto, содержащий данные для обработки
     * @return ChainAuthorizationDto после выполнения проверки параметров авторизации
     * @throws ru.otus.projectwork.user.exception.BadRequestException если номер клиента и номер паспорта null
     */
    @Override
    public ChainAuthorizationDto process(ChainAuthorizationDto chain) {

        AuthorizationRequestDto request=chain.getRequest();

        if (request.passportNumber()==null && request.mobilePhone()==null){
            throw new BadRequestException(AUTHORIZATION_PARAMS_NOT_VALID.getDescription());
        }

        return chain;
    }
}
