package ru.otus.projectwork.account.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.projectwork.account.exception.BadRequestException;
import ru.otus.projectwork.account.exception.NotFoundException;
import ru.otus.projectwork.account.exception.UnauthorizedUserException;

import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * <p>
 * Обрабатывает исключения, возникающие в контроллерах, и возвращает соответствующие ответы клиенту.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Обрабатывает исключение BadRequestException и возвращает HTTP-ответ с кодом 400 Bad Request.
     *
     * @param runTimeEx исключение BadRequestException
     * @return ответ клиенту с сообщением об ошибке и кодом состояния 400
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestException(RuntimeException runTimeEx) {
        return new ErrorResponseDto(runTimeEx.getMessage());
    }

    /**
     * Обрабатывает исключение MethodArgumentNotValidException и возвращает HTTP-ответ с кодом 400 Bad Request.
     *
     * @param methodArgNotValidEx исключение MethodArgumentNotValidException
     * @return ответ клиенту с сообщением об ошибке и кодом состояния 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(MethodArgumentNotValidException methodArgNotValidEx) {
        return new ErrorResponseDto(methodArgNotValidEx.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", ")));
    }

    /**
     * Обрабатывает исключение UnauthorizedUserException и возвращает HTTP-ответ с кодом 401  UNAUTHORIZED.
     *
     * @param runTimeEx исключение RuntimeException
     * @return ответ клиенту с сообщением об ошибке и кодом состояния 401
     */
    @ExceptionHandler(UnauthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleUnauthorizedUserException(RuntimeException runTimeEx) {
        return new ErrorResponseDto(runTimeEx.getMessage());
    }

    /**
     * Обрабатывает исключение NotFoundException и возвращает HTTP-ответ с кодом 404 Not Found.
     *
     * @param runTimeEx исключение NotFoundException
     * @return ответ клиенту с сообщением об ошибке и кодом состояния 404
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFoundException(RuntimeException runTimeEx) {
        return new ErrorResponseDto(runTimeEx.getMessage());
    }

    /**
     * Обрабатывает исключение InternalServerError и возвращает HTTP-ответ с кодом 500 Internal Server Error.
     *
     * @param runTimeEx исключение InternalServerError
     * @return ответ клиенту с сообщением об ошибке и кодом состояния 500
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleRuntimeException(RuntimeException runTimeEx) {
        return new ErrorResponseDto(runTimeEx.getMessage());
    }

}
