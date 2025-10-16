package ru.otus.projectwork.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO для ответа на запрос авторизации.
 */
@Builder
@Schema(description = "Данные для ответа на запрос авторизации клиента")
public record AuthorizationResponseDto(

        @Schema(description = "Токен доступа", example =
                """
                eyJraWQiOiI5YmYzMWM3Zi1mMDYyLTMzNmEtOTZkMy1jOGJkMWY4ZjJmZjMiLCJlbmMiOiJBMTI4R0NNIi
                wiYWxnIjoiZGlyIn0..5rhRq1bmfVT16ZgH.QVWFMAqWC41j163rOkZKS8WwaxiY2ZMzsbzr3Hq_HyvZOD-hkrfA3Zx5VpPCuMERYtfCIs
                cLkScCl0VPRtQKUhAuIRlaho5KZjSyNmeCRAzhwJIeyzvIEE1GR5kUbsJ0R_6_uJGBXRhqt-jbiFXdIA4FCnUGK_4EY9n2jBG-G3yZndsDbjJwk
                nS_IoVo6WWC7td-eu9Q1btSQ9g.N10NEnqOFmx7bI2i26QYFg
                """)
        @NotBlank(message = "Токен доступа не может быть пустым")
        String accessToken,

        @Schema(description = "Токен обновления",  example =
                """
               eyJraWQiOiI5YmYzMWM3Zi1mMDYyLTMzNmEtOTZkMy1jOGJkMWY4ZjJmZjMiLCJlbmMiOiJBMTI4R0NNIi
               wiYWxnIjoiZGlyIn0..5rhRq1bmfVT16ZgH.QVWFMAqWC41j163rOkZKS8WwaxiY2ZMzsbzr3Hq_HyvZOD-hkrfA3Zx5VpPCuMERYtfCIs
               cLkScCl0VPRtQKUhAuIRlaho5KZjSyNmeCRAzhwJIeyzvIEE1GR5kUbsJ0R_6_uJGBXRhqt-jbiFXdIA4FCnUGK_4EY9n2jBG-G3yZndsDbjJwk
               nS_IoVo6WWC7td-eu9Q1btSQ9g.N10NEnqOFmx7bI2i26QYFg
               """)
        @NotBlank(message = "Токен обновления не может быть пустым")
        String refreshToken,

        @Schema(description = "Идентификатор клиента", example = "955204dd-cb45-4683-82e6-18fa04c3c03e")
        @NotBlank(message = "Идентификатор не может быть пустым")
        String clientId

) {
}
