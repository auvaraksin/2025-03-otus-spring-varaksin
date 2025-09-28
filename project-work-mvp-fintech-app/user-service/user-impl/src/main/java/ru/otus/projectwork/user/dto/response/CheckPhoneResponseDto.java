package ru.otus.projectwork.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * DTO для ответа создания кода otp.
 */
@Builder
@Schema(description = "Данные после создания кода otp для незарегистрированного пользователя")
public record CheckPhoneResponseDto(

        String mobilePhone
) {
}
