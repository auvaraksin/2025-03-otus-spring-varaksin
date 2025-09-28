package ru.otus.projectwork.account.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.otus.projectwork.domain.model.Currency;
import ru.otus.projectwork.account.service.AccountDetailsService;

import java.util.Random;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountDetailsServiceImpl implements AccountDetailsService {

    String ACCOUNT_NUMBER_DEFAULT_PREFIX = "4081701";

    int ACCOUNT_NUMBER_LENGTH = 20;

    int DIGIT = 10;

    @Override
    public char[] generateAccountNumber(Currency currency) {
        StringBuilder accountNumberString = new StringBuilder(ACCOUNT_NUMBER_DEFAULT_PREFIX);
        accountNumberString.append(currency.getCode().toString());
        Random random = new Random();
        while (accountNumberString.length() < ACCOUNT_NUMBER_LENGTH) {
            accountNumberString.append(random.nextInt(DIGIT));
        }
        return accountNumberString.toString().toCharArray();
    }
}
