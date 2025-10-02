package ru.otus.projectwork.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import ru.otus.projectwork.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dto для представления отчета об операциях по счёту
 */

@Builder
@Schema(description = "Форма отчета об операции")
public record AccountTransactionResponseDto(
        @Schema(description = "Идентификатор транзации", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Идентификатор счета", example = "987e6543-e89b-12d3-a456-426614174000")
        UUID accountId,

        @Schema(description = "Тип транзакции", example = "WITHDRAW")
        TransactionType type,

        @Schema(description = "Сумма транзакции", example = "1500.50")
        BigDecimal summa,

        @Schema(description = "Дата создания записи", example = "2023-10-10T14:48:00.000Z")
        LocalDateTime createdAt,

        @Schema(description = "Детали транзакции", example = "Service payment")
        String details,

        @Schema(description = "Местоположение совершения операции", example = "Moscow, Russia")
        String location
) {
}
