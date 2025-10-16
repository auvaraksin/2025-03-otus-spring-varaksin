package ru.otus.projectwork.account.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление ExceptionMessage предоставляет сообщения об исключениях, используемые в приложении для обработки
 * различных ошибок.
 */
@Getter
@AllArgsConstructor
public enum ExceptionMessage {

    ACCOUNT_NOT_FOUND_BY_ACCOUNT_ID("Счет с account id %s не найден."),
    CONDITION_NOT_FOUND_BY_ID("Условие с id %s не найдено"),
    CURRENCY_NOT_FOUND_BY_CURRENCY_TYPE("Валюта с именем %s не найдена"),
    DENIED_ACCESS("В доступе отказано"),
    NO_AVAILABLE_ACCOUNTS_FOUND("Доступные счета не найдены"),
    NOT_FOUND_TRANSACTIONS("Не найдено транзакций за выбранный период"),
    UNAUTHORIZED_USER("Отсутствует авторизация. Доступ запрещен");

    private final String description;
}
