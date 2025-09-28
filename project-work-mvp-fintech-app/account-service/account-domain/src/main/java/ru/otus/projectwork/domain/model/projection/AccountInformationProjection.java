package ru.otus.projectwork.domain.model.projection;

import lombok.Builder;
import ru.otus.projectwork.domain.model.enums.AccountStatus;
import ru.otus.projectwork.domain.model.enums.AccountType;
import ru.otus.projectwork.domain.model.enums.CurrencyType;
import ru.otus.projectwork.domain.model.enums.Period;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record AccountInformationProjection(

        UUID accountId,

        String accountNaming,

        AccountType type,

        char[] accountNumber,

        CurrencyType currencyType,

        BigDecimal currentBalance,

        Period period,

        AccountStatus status,

        BigDecimal percent,

        Boolean isPayoff
) {
}
