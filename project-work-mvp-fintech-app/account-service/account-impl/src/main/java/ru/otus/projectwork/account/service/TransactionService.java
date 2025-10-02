package ru.otus.projectwork.account.service;

import ru.otus.projectwork.account.dto.response.AccountTransactionResponseDto;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    /**
     * Получает отчет об оперциях по счёту
     *
     * @param accountId идентификатор счета
     * @return список транзакций
     */
    List<AccountTransactionResponseDto> getTransactionsByAccountId(UUID accountId);
}
