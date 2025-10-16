package ru.otus.projectwork.account.service;

import ru.otus.projectwork.domain.model.projection.ClientAccountsProjection;
import ru.otus.projectwork.account.dto.request.AccountCreateRequestDto;
import ru.otus.projectwork.account.dto.response.AccountCreateResponseDto;
import ru.otus.projectwork.account.dto.response.AccountInformationResponseDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    /**
     * Метод для создания счёта.
     *
     * @param request  информация для создания счёта.
     * @param clientId уникальный идентификатор клиента.
     * @return информация о созданном счёте.
     */
    AccountCreateResponseDto createAccount(AccountCreateRequestDto request, UUID clientId);

    /**
     * Метод выводит список счетов клиента.
     *
     * @param clientId уникальный идентификатор клиента.
     * @return список счетов доступных для списания.
     */
    List<ClientAccountsProjection> getListOfAccounts(UUID clientId);

    /**
     * Возвращает информацию о счёте клиента.
     *
     * @param accountId идентификатор счёта.
     */
    AccountInformationResponseDto viewAccountInformation(UUID accountId);

}
