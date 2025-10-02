package ru.otus.projectwork.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при некорректном запросе.
 * Это подкласс RuntimeException и может использоваться для обработки ситуаций, когда получен некорректный запрос.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Создает новый объект исключения с заданным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public BadRequestException(String message) {
        super(message);
    }
}
