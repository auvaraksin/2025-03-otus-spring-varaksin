package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.projectwork.domain.model.PassportData;

import java.util.UUID;

/**
 * Класс для осуществления операций чтения и записи данных о паспортных данных в базу данных
 */
public interface PassportDataRepository extends JpaRepository<PassportData, UUID> {
    boolean existsByPassportNumber(String passportNumber);
}
