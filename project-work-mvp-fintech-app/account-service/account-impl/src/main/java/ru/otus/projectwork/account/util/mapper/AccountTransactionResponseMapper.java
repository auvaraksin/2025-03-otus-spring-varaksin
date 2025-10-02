package ru.otus.projectwork.account.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.projectwork.account.dto.response.AccountTransactionResponseDto;
import ru.otus.projectwork.domain.model.Transaction;

@Mapper(componentModel = "spring")
public interface AccountTransactionResponseMapper {

    /**
     * Метод преобразует объекты типа Transaction в объекты типа AccountTransactionResponseDto
     *
     * @param transaction список объектов типа Transaction
     * @return список объектов AccountTransactionReportDto
     */
    @Mapping(target = "id", source = "transactionId")
    @Mapping(target = "accountId", source = "accountId.accountId")
    @Mapping(target = "type", source = "transactionType")
    @Mapping(target = "summa", source = "sum")
    @Mapping(target = "createdAt", source = "createDate")
    AccountTransactionResponseDto toAccountTransactionResponseDto(Transaction transaction);
}
