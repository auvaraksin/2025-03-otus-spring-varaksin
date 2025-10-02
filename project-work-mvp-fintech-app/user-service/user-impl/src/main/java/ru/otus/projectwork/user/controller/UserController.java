package ru.otus.projectwork.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.projectwork.user.dto.request.AuthorizationRequestDto;
import ru.otus.projectwork.user.dto.request.CheckOtpRequestDto;
import ru.otus.projectwork.user.dto.request.CheckRegistrationRequestDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.dto.response.AuthorizationResponseDto;
import ru.otus.projectwork.user.dto.response.CheckPhoneResponseDto;
import ru.otus.projectwork.user.dto.response.RegistrationResponseDto;
import ru.otus.projectwork.user.exception.handler.ErrorResponseDto;
import ru.otus.projectwork.user.service.ClientService;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    ClientService clientService;

    /**
     * Обрабатывает запрос на регистрацию нового пользователя.
     *
     * @param request данные для регистрации нового пользователя
     * @return уникальный идентификатор созданного пользователя
     */
    @PostMapping("/public/users/registration")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "409", description = "Пользователь с такими данными уже существует",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    public RegistrationResponseDto registration(@RequestBody @Valid RegistrationRequestDto request) {
        return clientService.registration(request);
    }

    /**
     * Обрабатывает запрос на проверку регистрации пользователя по номеру телефона.
     *
     * @param request dto с номером телефона клиента
     * @return ответ с номером телефона клиента
     */
    @PostMapping("/public/users/check-registration")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Проверка регистрации пользователя по номеру телефона")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "409", description = "Пользователь с такими данными уже существует",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    public CheckPhoneResponseDto checkRegistration(@RequestBody @Valid CheckRegistrationRequestDto request) {
        return clientService.checkRegistration(request);
    }

    /**
     * Обрабатывает запрос на авторизацию пользователя в приложении.
     *
     * @param request  данные для авторизации
     * @param response получает cookie с jwt
     * @return ответ включающий токен доступа, токен обновления, идентификатор клиента
     */
    @PostMapping("/public/users/authorization")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Авторизация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Пользователь не зарегистрирован",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    public AuthorizationResponseDto authorize(@RequestBody @Valid AuthorizationRequestDto request,
                                              HttpServletResponse response) {
        return clientService.authorize(request, response);
    }

    /**
     * Обрабатывает запрос на создание otp кода для зарегистрированного пользователя
     *
     * @param request данные для проверки регистрации
     * @return ответ с номером телефона клиента
     */
    @PostMapping("/auth/users/otp/creation")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Создание и передача OTP кода пользователю",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Код OTP успешно создан и передан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Отсутствует авторизация. Доступ запрещен",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Мобильный телефон не найден",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    public CheckPhoneResponseDto createOtp(@RequestBody @Valid CheckRegistrationRequestDto request) {
        return clientService.saveOtpCode(request);
    }

    /**
     * Обрабатывает запрос на проверку otp кода
     *
     * @param request данные для проверки
     * @return ответ с номером телефона клиента
     */
    @PostMapping("/auth/users/otp/verification")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Проверка OTP-кода",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Код OTP успешно верифицирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Отсутствует авторизация. Доступ запрещен",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Запись отсутствует в системе",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "409", description = "Введен неверный код",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "429", description = "Превышено количество попыток ввода",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    public CheckPhoneResponseDto checkOtp(@RequestBody @Valid CheckOtpRequestDto request) {
        return clientService.checkOtpCode(request);
    }

}
