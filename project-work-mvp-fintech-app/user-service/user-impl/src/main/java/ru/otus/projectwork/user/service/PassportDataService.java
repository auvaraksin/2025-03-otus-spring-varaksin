package ru.otus.projectwork.user.service;

/**
 * Сервис для работы с паспортными данными
 */
public interface PassportDataService {

    /**
     * Проверяет существование паспортных данных по номеру паспорта.
     *
     * @param passportNumber номер паспорта для проверки
     * @return true, если паспортные данные с указанным номером существуют в базе данных, иначе false
     */
    boolean existsByPassportNumber(String passportNumber);
}
