package ru.otus.projectwork.account.service;

import ru.otus.projectwork.domain.model.Currency;

/**
 * Сервис для работы с деталями счёта.
 */
public interface AccountDetailsService {

    /**
     * Метод для генерации номера счёта.
     * @param currency валюта счёта.
     * @return сгенерированный номер счёта.
     */
    char[] generateAccountNumber(Currency currency);
}
