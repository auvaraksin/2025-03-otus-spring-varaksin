package ru.otus.projectwork.account.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.projectwork.account.dto.response.AccountCreateResponseDto;
import ru.otus.projectwork.account.dto.response.AccountInformationResponseDto;
import ru.otus.projectwork.domain.model.Account;
import ru.otus.projectwork.domain.model.projection.AccountInformationProjection;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    /**
     * Возвращает объект с информацией о созданном счёте.
     *
     * @param account       созданный счёт.
     * @param accountNumber номер созданного счёта.
     * @return объект с информацией о созданном счёте.
     */
    @Mapping(source = "account.accountStatus", target = "status")
    @Mapping(source = "account.currentBalance", target = "currentBalance")
    AccountCreateResponseDto toAccountCreateResponseDto(Account account, char[] accountNumber);

    /**
     * Возвращает объект с информацией о счёте клиента.
     *
     * @param account объект Account
     * @return AccountInformationResponse
     */
    @Mapping(source = "accountId", target = "accountId")
    AccountInformationResponseDto toAccountInformationResponse(AccountInformationProjection account);
}
