package ru.otus.projectwork.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление, представляющее различные статусы счёта.
 * Включает три статуса: активный, замороженный, закрытый.
 * Каждый элемент перечисления имеет свое описание, которое указывает на статус счёта.
 */
@AllArgsConstructor
@Getter
public enum AccountStatus {

    ACTIVE("Активный"),
    CLOSED("Замороженный"),
    FROZEN("Закрытый");

    private final String description;
}
