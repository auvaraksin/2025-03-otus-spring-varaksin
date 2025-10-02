package ru.otus.projectwork.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, указывающее на конфликт при обработке запроса.
 * <p>
 * Возвращает HTTP-ответ с кодом 409 Conflict.
 * <p>
 * Используется для обозначения ситуаций, когда запрос не может быть выполнен из-за конфликта с текущим состоянием
 * ресурса.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    /**
     * Создает новый экземпляр ConflictException с заданным сообщением.
     *
     * @param message Сообщение об ошибке
     */
    public ConflictException(String message) {
        super(message);
    }
}
