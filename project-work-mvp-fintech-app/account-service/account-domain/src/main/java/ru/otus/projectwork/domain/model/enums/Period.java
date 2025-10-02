package ru.otus.projectwork.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Period {

    THREE_MONTHS("3 месяца"),
    SIX_MONTHS("6 месяцев"),
    NINE_MONTHS("9 месяцев"),
    FOURTEEN_MONTHS("14 месяцев"),
    INDEFINITELY("Неопределенное");

    private final String description;
}
