package ru.otus.projectwork.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransferStatus {

    IN_PROGRESSING("В процессе"),
    DONE("Отправлено"),
    CANCELLED("Отменено");

    private final String description;
}
