package ru.otus.projectwork.account.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.projectwork.domain.model.Account;
import ru.otus.projectwork.domain.model.AccountDetails;
import ru.otus.projectwork.domain.model.Condition;
import ru.otus.projectwork.domain.model.Currency;
import ru.otus.projectwork.domain.model.enums.AccountStatus;
import ru.otus.projectwork.domain.model.projection.AccountInformationProjection;
import ru.otus.projectwork.domain.model.projection.ClientAccountsProjection;
import ru.otus.projectwork.domain.repository.AccountDetailsRepository;
import ru.otus.projectwork.domain.repository.AccountRepository;
import ru.otus.projectwork.domain.repository.ConditionRepository;
import ru.otus.projectwork.domain.repository.CurrencyRepository;
import ru.otus.projectwork.account.dto.request.AccountCreateRequestDto;
import ru.otus.projectwork.account.dto.response.AccountCreateResponseDto;
import ru.otus.projectwork.account.dto.response.AccountInformationResponseDto;
import ru.otus.projectwork.account.exception.NotFoundException;
import ru.otus.projectwork.account.service.AccountDetailsService;
import ru.otus.projectwork.account.service.AccountService;
import ru.otus.projectwork.account.util.DefaultAccountDetailsProperties;
import ru.otus.projectwork.account.util.mapper.AccountDetailsMapper;
import ru.otus.projectwork.account.util.mapper.AccountMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static ru.otus.projectwork.account.util.ExceptionMessage.ACCOUNT_NOT_FOUND_BY_ACCOUNT_ID;
import static ru.otus.projectwork.account.util.ExceptionMessage.CONDITION_NOT_FOUND_BY_ID;
import static ru.otus.projectwork.account.util.ExceptionMessage.CURRENCY_NOT_FOUND_BY_CURRENCY_TYPE;
import static ru.otus.projectwork.account.util.ExceptionMessage.NO_AVAILABLE_ACCOUNTS_FOUND;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    AccountDetailsService accountDetailsService;

    AccountDetailsRepository accountDetailsRepository;

    AccountRepository accountRepository;

    CurrencyRepository currencyRepository;

    ConditionRepository conditionRepository;

    AccountDetailsMapper accountDetailsMapper;

    AccountMapper accountMapper;

    DefaultAccountDetailsProperties defaultAccountDetailsProperties;

    BigDecimal DEFAULT_ACCOUNT_BALANCE = BigDecimal.ZERO;
    AccountStatus DEFAULT_ACCOUNT_STATUS = AccountStatus.ACTIVE;

    @Override
    @Transactional
    public AccountCreateResponseDto createAccount(AccountCreateRequestDto request, UUID clientId) {
        Currency currency = currencyRepository.findByType(
                        request.currency())
                .orElseThrow(() -> new NotFoundException(
                        CURRENCY_NOT_FOUND_BY_CURRENCY_TYPE.getDescription()
                                .formatted(request.currency())));

        AccountDetails accountDetails = new AccountDetails();
        char[] accountNumber = accountDetailsService.generateAccountNumber(currency);
        accountDetails = accountDetailsMapper.toAccountDetails(defaultAccountDetailsProperties);
        accountDetails.setAccountNumber(accountNumber);
        accountDetails = accountDetailsRepository.save(accountDetails);
        Condition condition = conditionRepository.findById(request.conditionsId())
                .orElseThrow(() -> new NotFoundException(CONDITION_NOT_FOUND_BY_ID.getDescription()
                        .formatted(request.conditionsId())));

        Account account = Account.builder()
                .clientId(clientId)
                .currency(currency)
                .accountDetails(accountDetails)
                .condition(condition)
                .currentBalance(DEFAULT_ACCOUNT_BALANCE)
                .accountStatus(DEFAULT_ACCOUNT_STATUS)
                .accountType(request.type())
                .isMaster(false)
                .build();

        account = accountRepository.save(account);
        return accountMapper.toAccountCreateResponseDto(account, accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientAccountsProjection> getListOfAccounts(UUID clientId) {
        List<ClientAccountsProjection> accounts = accountRepository.findAccountsByClientId(clientId);
        if (accounts.isEmpty()) {
            throw new NotFoundException(NO_AVAILABLE_ACCOUNTS_FOUND.getDescription());
        }
        return accounts;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountInformationResponseDto viewAccountInformation(UUID accountId) {

        AccountInformationProjection accountInformationProjection
                = accountRepository.viewAccountInformation(accountId).orElseThrow(
                () -> new NotFoundException(ACCOUNT_NOT_FOUND_BY_ACCOUNT_ID.getDescription()
                        .formatted(accountId)));

        return accountMapper.toAccountInformationResponse(accountInformationProjection);
    }
}
