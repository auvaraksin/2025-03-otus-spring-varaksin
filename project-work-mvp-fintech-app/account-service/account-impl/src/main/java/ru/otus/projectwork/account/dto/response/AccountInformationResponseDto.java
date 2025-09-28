package ru.otus.projectwork.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.otus.projectwork.domain.model.enums.AccountStatus;
import ru.otus.projectwork.domain.model.enums.AccountType;
import ru.otus.projectwork.domain.model.enums.CurrencyType;
import ru.otus.projectwork.domain.model.enums.Period;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Dto содержит детальную информацию по счету.
 */
@Builder
@Schema(description = "Реквизита счёта.")
public record AccountInformationResponseDto(

        @Schema(description = "Идентификатор счёта", example = "311e4567-e89b-12d3-a456-426614174015")
        UUID accountId,

        @Schema(description = "Название счёта", example = "Накопительный счёт")
        String accountNaming,

        @Schema(description = "Тип счёта", example = "SAVING")
        AccountType type,

        @Schema(description = "Номер счёта", example = "3214344rgerg231231ffwefewFWE")
        char[] accountNumber,

        @Schema(description = "Выбранная валюта счёта", example = "RUB")
        CurrencyType currencyType,

        @Schema(description = "Баланс счёта", example = "34000")
        BigDecimal currentBalance,

        @Schema(description = "Срок действия счёта", example = "3 month")
        Period period,

        @Schema(description = "Статус работы счёта", example = "ACTIVE")
        AccountStatus status,

        @Schema(description = "Процентная ставка", example = "4.8")
        BigDecimal percent,

        @Schema(description = "Выплата процентов", example = "true")
        Boolean isPayoff
) {
}
