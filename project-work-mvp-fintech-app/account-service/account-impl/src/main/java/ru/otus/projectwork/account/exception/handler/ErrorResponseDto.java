package ru.otus.projectwork.account.exception.handler;

/**
 * DTO для представления сообщения об ошибке.
 * Используется для передачи сообщений об ошибках от сервисов или контроллеров.
 */
public record ErrorResponseDto(String errorMessage) {
}
