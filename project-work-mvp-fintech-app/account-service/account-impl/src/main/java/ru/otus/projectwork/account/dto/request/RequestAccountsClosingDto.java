package ru.otus.projectwork.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Dto содержит идентификатор счёта.
 */
public record RequestAccountsClosingDto(

        @Schema(description = "Идентификатор счёта", example = "311e4567-e89b-12d3-a456-426614174015")
        UUID accountId) {
}
