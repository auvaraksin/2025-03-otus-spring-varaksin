package ru.otus.projectwork.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при большом количестве запросов.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestException extends RuntimeException {

  /**
   * Создает новый объект исключения с заданным сообщением.
   *
   * @param message сообщение об ошибке
   */
  public TooManyRequestException(String message) {
    super(message);
  }
}
