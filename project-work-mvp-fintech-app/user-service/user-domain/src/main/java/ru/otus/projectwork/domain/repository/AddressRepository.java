package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.projectwork.domain.model.Address;

import java.util.UUID;

/**
 * Класс для осуществления операций чтения и записи данных об адресе клиента в базу данных
 */
public interface AddressRepository extends JpaRepository<Address, UUID> {
}
