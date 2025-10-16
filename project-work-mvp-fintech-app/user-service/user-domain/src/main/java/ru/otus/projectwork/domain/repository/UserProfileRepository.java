package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.projectwork.domain.model.UserProfile;

import java.util.UUID;

/**
 * Класс для осуществления операций чтения и записи данных о профиле клиента в базу данных
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
