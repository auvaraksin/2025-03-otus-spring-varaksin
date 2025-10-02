package ru.otus.projectwork.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Данные для ответа на запрос регистрации пользователя")
public record RegistrationResponseDto (

        @Schema(description = "Идентификатор", example = "955204dd-cb45-4683-82e6-18fa04c3c03e")
        @NotBlank(message = "Идентификатор не может быть пустым")
        UUID id
) {
}
