package ru.otus.projectwork.user.service.chain.authorization.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.domain.repository.ClientRepository;
import ru.otus.projectwork.user.dto.ChainAuthorizationDto;
import ru.otus.projectwork.user.service.chain.authorization.ProcessAuthorizationChainPart;

import java.util.Optional;

import static ru.otus.projectwork.user.service.chain.authorization.impl.GettingClientInfoChainPart.ORDER;

/**
 * Часть цепочки для получения информации о клиенте из базы данных.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class GettingClientInfoChainPart implements ProcessAuthorizationChainPart {

    protected static final int ORDER = 2;

    private final ClientRepository clientRepository;

    /**
     * Выполняет получение информации о клиенте (имя клиента, фамилию, идентификатор, пароль),
     * в зависимости от переданных параметров поиска, поиск клиента в базе данных может осуществляться по паролю и
     * телефону либо по паролю и номеру паспорта. Один из параметров поиска моет быть null, в этом случае поиск происходит
     * по другому параметру.
     *
     * @param chain объект ChainAuthorizationDto, содержащий данные для обработки
     * @return ChainAuthorizationDto после выполнения поиска информации о клинте
     */
    @Override
    public ChainAuthorizationDto process(ChainAuthorizationDto chain) {

        String passportNumber = chain.getRequest().passportNumber();
        Optional<String> optionalMobilePhone = Optional.ofNullable(chain.getRequest().mobilePhone());

        optionalMobilePhone.ifPresentOrElse(
                value ->chain.setUserInfo(clientRepository.findAuthorizationInfoByMobilePhone(value)),
                () -> chain.setUserInfo(clientRepository.findAuthorizationInfoByPassportNumber(passportNumber))
        );

        return chain;
    }
}
