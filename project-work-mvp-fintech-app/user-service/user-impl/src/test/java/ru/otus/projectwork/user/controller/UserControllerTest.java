package ru.otus.projectwork.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.projectwork.user.dto.request.AuthorizationRequestDto;
import ru.otus.projectwork.user.dto.request.CheckOtpRequestDto;
import ru.otus.projectwork.user.dto.request.CheckRegistrationRequestDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.dto.response.AuthorizationResponseDto;
import ru.otus.projectwork.user.dto.response.CheckPhoneResponseDto;
import ru.otus.projectwork.user.dto.response.RegistrationResponseDto;
import ru.otus.projectwork.user.exception.ConflictException;
import ru.otus.projectwork.user.exception.InternalServerError;
import ru.otus.projectwork.user.exception.NotFoundException;
import ru.otus.projectwork.user.exception.TooManyRequestException;
import ru.otus.projectwork.user.service.ClientService;
import ru.otus.projectwork.user.util.ExceptionMessage;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Тесты для UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void whenValidRegistrationRequest_thenReturnRegistrationResponse() throws Exception {
        // Given
        RegistrationRequestDto request = createValidRegistrationRequest();
        UUID expectedId = UUID.randomUUID();
        RegistrationResponseDto response = RegistrationResponseDto.builder().id(expectedId).build();

        when(clientService.registration(any(RegistrationRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/public/users/registration")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedId.toString()));
    }

    @Test
    @DisplayName("Регистрация - конфликт при существующем пользователе")
    void whenUserAlreadyExists_thenReturnConflict() throws Exception {
        // Given
        RegistrationRequestDto request = createValidRegistrationRequest();

        when(clientService.registration(any(RegistrationRequestDto.class)))
                .thenThrow(new ConflictException(ExceptionMessage.CLIENT_WITH_PASSPORT_NUMBER_EXIST.getDescription()));

        // When & Then
        mockMvc.perform(post("/public/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @DisplayName("Регистрация - внутренняя ошибка сервера")
    void whenInternalErrorDuringRegistration_thenReturnInternalServerError() throws Exception {
        // Given
        RegistrationRequestDto request = createValidRegistrationRequest();

        when(clientService.registration(any(RegistrationRequestDto.class)))
                .thenThrow(new InternalServerError("Internal server error"));

        // When & Then
        mockMvc.perform(post("/public/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @DisplayName("Успешная проверка регистрации - пользователь не зарегистрирован")
    void whenCheckRegistrationAndUserNotExists_thenReturnSuccess() throws Exception {
        // Given
        CheckRegistrationRequestDto request = CheckRegistrationRequestDto.builder()
                .mobilePhone("73456789010")
                .build();
        CheckPhoneResponseDto response = CheckPhoneResponseDto.builder()
                .mobilePhone("73456789010")
                .build();

        when(clientService.checkRegistration(any(CheckRegistrationRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/public/users/check-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobilePhone").value("73456789010"));
    }

    @Test
    @DisplayName("Проверка регистрации - конфликт при существующем пользователе")
    void whenCheckRegistrationAndUserExists_thenReturnConflict() throws Exception {
        // Given
        CheckRegistrationRequestDto request = CheckRegistrationRequestDto.builder()
                .mobilePhone("73456789010")
                .build();

        when(clientService.checkRegistration(any(CheckRegistrationRequestDto.class)))
                .thenThrow(new ConflictException(ExceptionMessage.MOBILE_PHONE_REGISTERED.getDescription()));

        // When & Then
        mockMvc.perform(post("/public/users/check-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    void whenValidAuthorizationRequest_thenReturnAuthorizationResponse() throws Exception {
        // Given
        AuthorizationRequestDto request = AuthorizationRequestDto.builder()
                .mobilePhone("73456789010")
                .password("SecurePass123!")
                .passportNumber("1234567890")
                .build();

        AuthorizationResponseDto response = AuthorizationResponseDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .clientId(UUID.randomUUID().toString())
                .build();

        when(clientService.authorize(any(AuthorizationRequestDto.class), any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/public/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.clientId").exists());
    }

    @Test
    @DisplayName("Авторизация - пользователь не найден")
    void whenAuthorizationUserNotFound_thenReturnNotFound() throws Exception {
        // Given
        AuthorizationRequestDto request = AuthorizationRequestDto.builder()
                .mobilePhone("73456789010")
                .password("SecurePass123!")
                .passportNumber("1234567890")
                .build();

        when(clientService.authorize(any(AuthorizationRequestDto.class), any()))
                .thenThrow(new NotFoundException(ExceptionMessage.CLIENT_BY_MOBILE_PHONE_NOT_FOUND.getDescription()));

        // When & Then
        mockMvc.perform(post("/public/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Успешное создание OTP кода")
    void whenCreateOtpWithValidToken_thenReturnSuccess() throws Exception {
        // Given
        CheckRegistrationRequestDto request = CheckRegistrationRequestDto.builder()
                .mobilePhone("73456789010")
                .build();
        CheckPhoneResponseDto response = CheckPhoneResponseDto.builder()
                .mobilePhone("73456789010")
                .build();

        when(clientService.saveOtpCode(any(CheckRegistrationRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/users/otp/creation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobilePhone").value("73456789010"));
    }

    @Test
    @DisplayName("Создание OTP - отсутствие авторизации")
    void whenCreateOtpWithoutAuth_thenReturnUnauthorized() throws Exception {
        // Given
        CheckRegistrationRequestDto request = CheckRegistrationRequestDto.builder()
                .mobilePhone("73456789010")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/users/otp/creation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Создание OTP - телефон не найден")
    void whenCreateOtpPhoneNotFound_thenReturnNotFound() throws Exception {
        // Given
        CheckRegistrationRequestDto request = CheckRegistrationRequestDto.builder()
                .mobilePhone("73456789010")
                .build();

        when(clientService.saveOtpCode(any(CheckRegistrationRequestDto.class)))
                .thenThrow(new NotFoundException(ExceptionMessage.CLIENT_BY_MOBILE_PHONE_NOT_FOUND.getDescription()));

        // When & Then
        mockMvc.perform(post("/auth/users/otp/creation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Успешная проверка OTP кода")
    void whenCheckOtpWithValidCode_thenReturnSuccess() throws Exception {
        // Given
        CheckOtpRequestDto request = CheckOtpRequestDto.builder()
                .mobilePhone("73456789010")
                .otpCode("123456")
                .build();
        CheckPhoneResponseDto response = CheckPhoneResponseDto.builder()
                .mobilePhone("73456789010")
                .build();

        when(clientService.checkOtpCode(any(CheckOtpRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/auth/users/otp/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobilePhone").value("73456789010"));
    }

    @Test
    @DisplayName("Проверка OTP - отсутствие авторизации")
    void whenCheckOtpWithoutAuth_thenReturnUnauthorized() throws Exception {
        // Given
        CheckOtpRequestDto request = CheckOtpRequestDto.builder()
                .mobilePhone("73456789010")
                .otpCode("123456")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/users/otp/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка OTP - неверный код")
    void whenCheckOtpWithInvalidCode_thenReturnConflict() throws Exception {
        // Given
        CheckOtpRequestDto request = CheckOtpRequestDto.builder()
                .mobilePhone("73456789010")
                .otpCode("wrong")
                .build();

        when(clientService.checkOtpCode(any(CheckOtpRequestDto.class)))
                .thenThrow(new ConflictException(ExceptionMessage.INCORRECT_CODE_ENTERED.getDescription()));

        // When & Then
        mockMvc.perform(post("/auth/users/otp/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка OTP - превышено количество попыток")
    void whenCheckOtpTooManyAttempts_thenReturnTooManyRequests() throws Exception {
        // Given
        CheckOtpRequestDto request = CheckOtpRequestDto.builder()
                .mobilePhone("73456789010")
                .otpCode("123456")
                .build();

        when(clientService.checkOtpCode(any(CheckOtpRequestDto.class)))
                .thenThrow(new TooManyRequestException(ExceptionMessage.MANY_REQUEST.getDescription()));

        // When & Then
        mockMvc.perform(post("/auth/users/otp/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка OTP - запись отсутствует в системе")
    void whenCheckOtpRecordNotFound_thenReturnNotFound() throws Exception {
        // Given
        CheckOtpRequestDto request = CheckOtpRequestDto.builder()
                .mobilePhone("73456789010")
                .otpCode("123456")
                .build();

        when(clientService.checkOtpCode(any(CheckOtpRequestDto.class)))
                .thenThrow(new NotFoundException(ExceptionMessage.NO_RECORD_IN_THE_SYSTEM.getDescription()));

        // When & Then
        mockMvc.perform(post("/auth/users/otp/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @ParameterizedTest
    @MethodSource("invalidRegistrationRequests")
    @DisplayName("Регистрация - некорректные данные запроса")
    void whenInvalidRegistrationRequest_thenReturnBadRequest(RegistrationRequestDto invalidRequest) throws Exception {
        mockMvc.perform(post("/public/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidAuthorizationRequests")
    @DisplayName("Авторизация - некорректные данные запроса")
    void whenInvalidAuthorizationRequest_thenReturnBadRequest(AuthorizationRequestDto invalidRequest) throws Exception {
        mockMvc.perform(post("/public/users/authorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // Вспомогательные методы
    private RegistrationRequestDto createValidRegistrationRequest() {
        return RegistrationRequestDto.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .middleName("Иванович")
                .mobilePhone("73456789010")
                .email("user@example.com")
                .password("SecurePass123!")
                .passportNumber("1234567890")
                .issuedBy("Отделение полиции")
                .issueDate(LocalDate.of(2010, 1, 1))
                .departmentCode("770001")
                .birthDate(LocalDate.of(1990, 5, 20))
                .country("Россия")
                .city("Москва")
                .street("Арбатская")
                .house("10")
                .hull("1")
                .flat("5")
                .postCode("123456")
                .build();
    }

    private static Stream<Arguments> invalidRegistrationRequests() {
        return Stream.of(
                // Пустое имя
                Arguments.of(RegistrationRequestDto.builder()
                        .firstName("")
                        .lastName("Иванов")
                        .mobilePhone("73456789010")
                        .email("user@example.com")
                        .password("SecurePass123!")
                        .passportNumber("1234567890")
                        .issuedBy("Отделение полиции")
                        .issueDate(LocalDate.of(2010, 1, 1))
                        .departmentCode("770001")
                        .birthDate(LocalDate.of(1990, 5, 20))
                        .country("Россия")
                        .city("Москва")
                        .street("Арбатская")
                        .house("10")
                        .postCode("123456")
                        .build()),
                // Неверный email
                Arguments.of(RegistrationRequestDto.builder()
                        .firstName("Иван")
                        .lastName("Иванов")
                        .mobilePhone("73456789010")
                        .email("invalid-email")
                        .password("SecurePass123!")
                        .passportNumber("1234567890")
                        .issuedBy("Отделение полиции")
                        .issueDate(LocalDate.of(2010, 1, 1))
                        .departmentCode("770001")
                        .birthDate(LocalDate.of(1990, 5, 20))
                        .country("Россия")
                        .city("Москва")
                        .street("Арбатская")
                        .house("10")
                        .postCode("123456")
                        .build()),
                // Слишком короткий пароль
                Arguments.of(RegistrationRequestDto.builder()
                        .firstName("Иван")
                        .lastName("Иванов")
                        .mobilePhone("73456789010")
                        .email("user@example.com")
                        .password("short")
                        .passportNumber("1234567890")
                        .issuedBy("Отделение полиции")
                        .issueDate(LocalDate.of(2010, 1, 1))
                        .departmentCode("770001")
                        .birthDate(LocalDate.of(1990, 5, 20))
                        .country("Россия")
                        .city("Москва")
                        .street("Арбатская")
                        .house("10")
                        .postCode("123456")
                        .build())
        );
    }

    private static Stream<Arguments> invalidAuthorizationRequests() {
        return Stream.of(
                // Пустой пароль
                Arguments.of(AuthorizationRequestDto.builder()
                        .mobilePhone("73456789010")
                        .password("")
                        .passportNumber("1234567890")
                        .build()),
                // Неверный формат телефона
                Arguments.of(AuthorizationRequestDto.builder()
                        .mobilePhone("invalid-phone")
                        .password("SecurePass123!")
                        .passportNumber("1234567890")
                        .build()),
                // Слишком длинный номер паспорта
                Arguments.of(AuthorizationRequestDto.builder()
                        .mobilePhone("73456789010")
                        .password("SecurePass123!")
                        .passportNumber("12345678901") // 11 символов
                        .build())
        );
    }
}
