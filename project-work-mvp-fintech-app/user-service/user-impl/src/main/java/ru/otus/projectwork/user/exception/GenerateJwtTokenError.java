package ru.otus.projectwork.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при ошибке генерации JWT токена.
 * Это подкласс RuntimeException и может использоваться для обработки ситуаций, когда получен некорректный запрос.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenerateJwtTokenError extends RuntimeException {

    /**
     * Создает новый объект исключения с заданным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public GenerateJwtTokenError(String message) {
        super(message);
    }
}
