package ru.otus.projectwork.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление, представляющее различные типы счёта.
 * Включает четыре типа: текущий, сберегательный, расчетный, депозитный.
 */
@AllArgsConstructor
@Getter
public enum AccountType {

    CURRENT("Текущий счет"),
    SAVING("Сберегательный счет"),
    CHЕCKING("Расчетный счет"),
    DEPOSIT("Депозитный счет");

    private final String description;
}
