package ru.otus.projectwork.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое в случае ошибки на стороне сервера.
 * Это подкласс RuntimeException и может использоваться для обработки ситуаций, когда
 * запрос не выполнен в результате ошибки сервера.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError extends RuntimeException {

    /**
     * Создает новый объект исключения с заданным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public InternalServerError(String message) {
        super(message);
    }
}
