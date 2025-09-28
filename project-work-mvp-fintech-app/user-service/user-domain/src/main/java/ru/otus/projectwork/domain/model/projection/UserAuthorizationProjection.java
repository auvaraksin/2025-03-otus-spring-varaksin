package ru.otus.projectwork.domain.model.projection;

import java.util.UUID;

/**
 * Проекция для сущности Client и связанных с ней UserProfile, PassportData.
 * Интерфейс используется для выбора поле id, firstName, lastName полей из сущности
 * Client, и password из сущности UserProfile, чтобы оптимизировать запросы и уменьшить объем загружаемых данных.
 */
public interface UserAuthorizationProjection {

    UUID getId();

    String getFirstName();

    String getLastName();

    String getPassword();

}
