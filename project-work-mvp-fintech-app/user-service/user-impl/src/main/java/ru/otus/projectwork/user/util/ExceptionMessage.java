package ru.otus.projectwork.user.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление ExceptionMessage предоставляет сообщения об исключениях, используемые в приложении для обработки
 * различных ошибок.
 */
@Getter
@AllArgsConstructor
public enum ExceptionMessage {

    ACCOUNT_DOES_NOT_EXISTS("Аккаунт не существует."),
    AUTHORIZATION_PARAMS_NOT_VALID("Некорректные запрос на авторизацию, номер паспорта и номер телефона null."),
    CLIENT_BY_MOBILE_PHONE_NOT_FOUND("Мобильный телефон %s не найден."),
    CLIENT_WITH_PASSPORT_NUMBER_EXIST("Клиент с таким номером паспорта уже существует."),
    COULD_NOT_GENERATE_TOKENS("Не удалось сгенерировать jwt токен."),
    DENIED_ACCESS("В доступе отказано"),
    INCORRECT_CODE_ENTERED("Введен неверный OTP-код"),
    INCORRECT_PASSWORD("Введен неверный пароль"),
    MANY_REQUEST("Превышено количество попыток ввода OTP-кода"),
    MOBILE_PHONE_REGISTERED("Пользователь с таким номером телефона уже существует"),
    NO_RECORD_IN_THE_SYSTEM("Запись отсутствует в системе, OTP-код не найден"),
    UNAUTHORIZED_USER("Отсутствует авторизация. Доступ запрещен");

    private final String description;
}
