package ru.otus.projectwork.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.projectwork.domain.model.Client;
import ru.otus.projectwork.domain.model.projection.UserAuthorizationProjection;

import java.util.Optional;
import java.util.UUID;

/**
 * Класс для осуществления операций чтения и записи данных о клиенте банка/пользователя приложения в базу данных
 */
public interface ClientRepository extends JpaRepository<Client, UUID> {

    /**
     * Метод используется для проверки существования клиента с номером телефона
     *
     * @param mobilePhone - номер телефона
     * @return true - если существует клиент с таким номером телефона false - если не существует клиента с таким номером
     * телефона
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Client c WHERE c.mobilePhone = :mobilePhone)")
    boolean isMobilePhoneExists(@Param("mobilePhone") String mobilePhone);

    /**
     * Метод используется для поиска имени, фамилии, идентификатора, пароля клиента по его номеру паспорта
     *
     * @param passportNumber - номер паспорта
     * @return Optional UserAuthorizationProjection информация для авторизации клиента
     */
    @Query
            (value = """
            SELECT c.id,c.first_name firstName,c.last_name lastName,up.password
            FROM client AS c
            JOIN user_profile AS up ON c.id=up.client_id
            JOIN passport_data AS pd ON c.passport_id=pd.id
            WHERE pd.passport_number=:passportNumber
            """, nativeQuery = true)
    Optional<UserAuthorizationProjection> findAuthorizationInfoByPassportNumber(@Param("passportNumber") String passportNumber);

    /**
     * Метод используется для поиска имени, фамилии, идентификатора, пароля клиента по его номеру телефона
     *
     * @param mobilePhone - номер телефона
     * @return Optional UserAuthorizationProjection информация для авторизации клиента
     */
    @Query
            (value = """
            SELECT c.id,c.first_name firstName,c.last_name lastName,up.password
            FROM user_profile AS up JOIN client AS c ON c.id=up.client_id
            WHERE c.mobile_phone=:mobilePhone
            """, nativeQuery = true)
    Optional<UserAuthorizationProjection> findAuthorizationInfoByMobilePhone(@Param("mobilePhone") String mobilePhone);

}