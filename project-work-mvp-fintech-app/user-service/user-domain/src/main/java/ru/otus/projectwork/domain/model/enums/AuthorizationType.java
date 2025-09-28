package ru.otus.projectwork.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление типов авторизации.
 * <p>
 * Данное перечисление определяет возможные типы авторизации для пользователя. Каждый тип
 * авторизации имеет описание, которое предоставляется в виде строки.
 * </p>
 * <p>
 * Пример использования:
 * <pre>
 * {@code
 * AuthorizationType type = AuthorizationType.LOGIN;
 * System.out.println(type.getDescription()); // Логин
 * }
 * </pre>
 * </p>
 */
@Getter
@AllArgsConstructor
public enum AuthorizationType {
    PIN("Пин-код"),
    LOGIN("Логин");

    private final String description;
}
