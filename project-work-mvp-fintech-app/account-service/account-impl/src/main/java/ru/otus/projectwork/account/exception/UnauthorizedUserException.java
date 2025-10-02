package ru.otus.projectwork.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при попытке неавторизованным пользователем получить доступ к ресурсам.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedUserException extends RuntimeException {

    /**
     * Создает новый объект исключения с заданным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
