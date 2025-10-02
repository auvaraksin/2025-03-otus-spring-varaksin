package ru.otus.projectwork.account.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.otus.projectwork.account.dto.response.AccountTransactionResponseDto;
import ru.otus.projectwork.account.service.TransactionService;
import ru.otus.projectwork.account.util.mapper.AccountTransactionResponseMapper;
import ru.otus.projectwork.domain.model.Transaction;
import ru.otus.projectwork.domain.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionRepository;

    AccountTransactionResponseMapper accountTransactionResponseMapper;

    @Override
    public List<AccountTransactionResponseDto> getTransactionsByAccountId(UUID accountId) {

        List<AccountTransactionResponseDto> resultTransactionList = new ArrayList<>();

        var transactions = transactionRepository.findAllByAccountId(accountId);

        for (Transaction transaction : transactions) {
            resultTransactionList.add(accountTransactionResponseMapper.toAccountTransactionResponseDto(transaction));
        }
        return resultTransactionList;
    }

}
