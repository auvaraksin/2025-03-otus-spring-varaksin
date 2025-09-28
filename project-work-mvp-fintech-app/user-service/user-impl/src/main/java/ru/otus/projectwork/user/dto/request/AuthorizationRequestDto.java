package ru.otus.projectwork.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.otus.projectwork.user.util.validator.RegExp;

/**
 * DTO для запроса на авторизацию пользователя.
 */
@Builder
@Schema(description = "Данные для проверки авторизации пользователя")
public record AuthorizationRequestDto(

        @Schema(description = "Пароль", example = "securePassword123!")
        @NotBlank(message = "Пароль не может быть пустым")
        @Pattern(message = "Пароль должен содержать минимум 1 букву верхнего и нижнего регистра, цифру и символ", regexp = RegExp.PASSWORD)
        @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
        String password,

        @Schema(description = "Номер паспорта", example = "1234567890")
        @Size(max = 10, message = "Номер паспорта не может превышать 10 символов")
        String passportNumber,

        @Schema(description = "Номер мобильного телефона", example = "73456789010")
        @Pattern(message = "Некорректный номер телефона", regexp = RegExp.MOBILE_PHONE_NUMBER_FOR_AUTHORISATION)
        String mobilePhone
) {
}
