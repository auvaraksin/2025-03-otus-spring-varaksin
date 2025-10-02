package ru.otus.projectwork.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.otus.projectwork.domain.model.enums.AccountType;
import ru.otus.projectwork.domain.model.enums.CurrencyType;

import java.util.UUID;

/**
 * Dto содержит информацию для создания счета.
 */
@Builder
@Schema(description = "Создание счета")
public record AccountCreateRequestDto(

        @Schema(description = "Тип счёта", example = "CURRENT")
        @NotNull(message = "type не может быть null")
        AccountType type,

        @Schema(description = "Валюта", example = "RUB")
        @NotNull(message = "currency не может быть null")
        CurrencyType currency,

        @Schema(description = "Идентификатор условий счёта", example = "158413e5-e556-46c5-bac5-73dd0c62e16e")
        @NotNull(message = "conditionsId не может быть null")
        UUID conditionsId
) {
}
