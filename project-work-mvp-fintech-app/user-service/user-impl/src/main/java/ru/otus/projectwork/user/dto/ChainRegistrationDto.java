package ru.otus.projectwork.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.otus.projectwork.domain.model.Address;
import ru.otus.projectwork.domain.model.Client;
import ru.otus.projectwork.domain.model.PassportData;
import ru.otus.projectwork.domain.model.UserProfile;

/**
 * DTO для передачи данных между частями цепочки регистрации.
 */
@Data
@Builder
@Schema(description = "DTO для передачи данных между частями цепочки регистрации")
public class ChainRegistrationDto {

    @Schema(description = "Паспортные данные клиента")
    private PassportData passportData;

    @Schema(description = "Адрес регистрации клиента")
    private Address addressRegistration;

    @Schema(description = "Фактический адрес клиента")
    private Address addressActual;

    @Schema(description = "Данные клиента")
    private Client client;

    @Schema(description = "Профиль пользователя")
    private UserProfile userProfile;

}
