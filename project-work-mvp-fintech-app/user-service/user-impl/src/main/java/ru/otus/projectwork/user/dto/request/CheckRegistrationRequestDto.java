package ru.otus.projectwork.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import ru.otus.projectwork.user.util.validator.RegExp;

/**
 * DTO для запроса проверки регистрации.
 */
@Builder
@Schema(description = "Данные для проверки регистрации пользователя")
public record CheckRegistrationRequestDto(

        @Schema(description = "Номер мобильного телефона", example = "73456789010")
        @NotBlank(message = "Номер телефона не может быть пустым")
        @Pattern(message = "Некорректные данные запроса", regexp = RegExp.MOBILE_PHONE_NUMBER)
        String mobilePhone
) {
}
