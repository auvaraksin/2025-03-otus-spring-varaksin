package ru.otus.projectwork.domain.model.projection;

import lombok.Builder;
import ru.otus.projectwork.domain.model.enums.AccountType;
import ru.otus.projectwork.domain.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ClientAccountsProjection(

        UUID id,

        char[] accountNumber,

        BigDecimal currentBalance,

        CurrencyType currency,

        Boolean isMaster,

        AccountType type
) {
}
