package ru.otus.projectwork.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление, представляющее различные коды валют.
 * Включает два кода валюты: код рубля для проведения международных операций, код рубля для проведения платежей внутри страны.
 */
@AllArgsConstructor
@Getter
public enum CurrencyType {

    RUB("Российский рублях"),
    USD("Доллар США"),
    EUR("Евро");

    private final String description;
}