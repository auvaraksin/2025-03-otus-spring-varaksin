package ru.otus.projectwork.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import ru.otus.projectwork.user.util.validator.RegExp;

/**
 * DTO для запроса сверки OTP-кода.
 *
 * @param mobilePhone номер телефона клиента
 * @param otpCode     OTP-код
 */
@Builder
@Schema(description = "Данные для сверки OTP-кода")
public record CheckOtpRequestDto(

        @Schema(description = "Номер мобильного телефона", example = "73456789010")
        @NotBlank(message = "Номер телефона не может быть пустым")
        @Pattern(message = "Некорректный номер телефона", regexp = RegExp.MOBILE_PHONE_NUMBER)
        String mobilePhone,

        @Schema(description = "OTP-код полученный клиентом")
        String otpCode
) {
}
