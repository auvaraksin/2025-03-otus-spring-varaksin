package ru.otus.projectwork.user.service;

import jakarta.servlet.http.HttpServletResponse;
import ru.otus.projectwork.user.dto.request.AuthorizationRequestDto;
import ru.otus.projectwork.user.dto.request.CheckOtpRequestDto;
import ru.otus.projectwork.user.dto.request.CheckRegistrationRequestDto;
import ru.otus.projectwork.user.dto.request.RegistrationRequestDto;
import ru.otus.projectwork.user.dto.response.AuthorizationResponseDto;
import ru.otus.projectwork.user.dto.response.CheckPhoneResponseDto;
import ru.otus.projectwork.user.dto.response.RegistrationResponseDto;

public interface ClientService {

    /**
     * Регистрирует нового клиента.
     * <p>
     * Проверяет, существует ли клиент с данным номером паспорта. Если да, выбрасывает исключение ConflictException.
     * Использует цепочку ответственности для обработки данных регистрации и возвращает ответ с ID клиента.
     * </p>
     *
     * @param request данные запроса регистрации
     * @return ответ с ID зарегистрированного клиента
     * @throws ru.otus.projectwork.user.exception.ConflictException если клиент с данным номером паспорта уже существует
     */
    RegistrationResponseDto registration(RegistrationRequestDto request);

    /**
     * Проверяет регистрацию клиента по номеру телефона и возвращает номером телефона клиента.
     *
     * @param request dto с номером телефона клиента
     * @return CheckPhoneResponseDto объект с номером телефона клиента
     * @throws ru.otus.projectwork.user.exception.ConflictException если пользователь с таким номером уже существует
     */
    CheckPhoneResponseDto checkRegistration(CheckRegistrationRequestDto request);

    /**
     * Выполняет авторизацию клиента в приложении.
     * <p>
     * Ищет клиента по номеру телефона либо по номеру паспорта. Если клиент не найден, выбрасывает исключение
     * NotFoundException. Возвращает токен доступа , токен обновления и идентификатор клиента.
     * </p>
     *
     * @param request  запрос на проверку регистрации
     * @param response для передачи jwt в cookie
     * @return ответ включающий токен доступа, токен обновления и идентификатор клиента
     * @throws ru.otus.projectwork.user.exception.NotFoundException     если клиент с данным номером телефона не найден
     * @throws ru.otus.projectwork.user.exception.BadRequestException   если номер телефона и паспорта null, либо несоответствия введенного пароля c
     *                                                                  паролем из базы данных
     * @throws ru.otus.projectwork.user.exception.GenerateJwtTokenError в случае ошибки при получении токенов
     */
    AuthorizationResponseDto authorize(AuthorizationRequestDto request, HttpServletResponse response);

    /**
     * Проверка номера и сохранение otp кода для зарегистрированного пользователя.
     * <p>
     * Проверяет наличие номера телефона. Если номер найден, то создает запись в Redis с
     * OTP кодом и отправляет сообщение пользователю.
     * В рамках MVP сервис отправки реализован через вывод сообщения с отображением OTP кода
     * в консоли приложения.
     * </p>
     *
     * @param request с номером телефона для сохранения в Redis в качестве ключа
     * @return dto c номером телефона.
     * @throws ru.otus.projectwork.user.exception.NotFoundException если номер телефона клиента не найден
     */
    CheckPhoneResponseDto saveOtpCode(CheckRegistrationRequestDto request);

    /**
     * Проверка OTP-кода.
     * <p>
     * Сверяет полученные данные с данными из Redis.
     * </p>
     *
     * @param request запрос на сверку кода otp c данными из Redis.
     * @return dto c номером телефона.
     * @throws ru.otus.projectwork.user.exception.NotFoundException       если данные отсутствуют в бд
     * @throws ru.otus.projectwork.user.exception.TooManyRequestException если количество запросов на проверку больше допустимого
     * @throws ru.otus.projectwork.user.exception.ConflictException       если данные при сверке не совпадают
     */
    CheckPhoneResponseDto checkOtpCode(CheckOtpRequestDto request);
}
