package ru.otus.projectwork.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.otus.projectwork.domain.model.enums.AccountStatus;

import java.math.BigDecimal;

/**
 * Dto содержит информацию о созданном счёте.
 */
@Builder
@Schema(description = "Информация о созданном счёте")
public record AccountCreateResponseDto(

        @Schema(description = "Номер счёта", example = "40817840800000000870")
        @NotNull(message = "accountNumber не может быть null")
        char[] accountNumber,

        @Schema(description = "Тип счёта", example = "CURRENT")
        @NotNull(message = "status не может быть null")
        AccountStatus status,

        @Schema(description = "Текущий баланс", example = "15000")
        @NotNull(message = "currentBalance не может быть null")
        BigDecimal currentBalance
) {
}
