package ru.otus.projectwork.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.otus.projectwork.domain.model.projection.UserAuthorizationProjection;
import ru.otus.projectwork.user.dto.request.AuthorizationRequestDto;

import java.util.Optional;
import java.util.UUID;

/**
 * DTO для передачи данных между частями цепочки авторизации.
 */
@Data
@Builder
@Schema(description = "DTO для передачи данных между частями цепочки авторизации")
public class ChainAuthorizationDto {

    @Schema(description = "Запрос на авторизацию клиента")
    private AuthorizationRequestDto request;

    @Schema(description = "Объект обернутый в Optional который содержит информацию о клиенте полученную из базы данных")
    private Optional<UserAuthorizationProjection> userInfo;

    @Schema(description = "Id клиента")
    private UUID clientId;

    @Schema(description = "Полное имя пользователя")
    private String userFullName;

    @Schema(description = "Токен доступа")
    private String accessToken;

    @Schema(description = "Токен обновления")
    private String refreshToken;

}
