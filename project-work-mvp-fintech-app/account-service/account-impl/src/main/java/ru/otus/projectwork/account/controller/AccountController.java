package ru.otus.projectwork.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.projectwork.account.dto.request.AccountCreateRequestDto;
import ru.otus.projectwork.account.dto.response.AccountCreateResponseDto;
import ru.otus.projectwork.account.dto.response.AccountInformationResponseDto;
import ru.otus.projectwork.account.dto.response.AccountTransactionResponseDto;
import ru.otus.projectwork.account.exception.handler.ErrorResponseDto;
import ru.otus.projectwork.account.service.AccountService;
import ru.otus.projectwork.account.service.TransactionService;
import ru.otus.projectwork.domain.model.projection.ClientAccountsProjection;

import java.util.List;
import java.util.UUID;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountController {

    AccountService accountService;

    TransactionService transactionService;

    /**
     * Создание нового счета.
     *
     * @param request  информация о создаваемом счете.
     * @param clientId уникальный идентификатор клиента.
     * @return информация о созданном счете.
     */
    @Operation(summary = "Создание счета")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счёт создан успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Отсутствует авторизация. Доступ запрещен",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))})})
    @PostMapping("/auth/accounts")
    @ResponseStatus(HttpStatus.OK)
    public AccountCreateResponseDto createAccount(
            @RequestBody @Valid AccountCreateRequestDto request, @RequestHeader UUID clientId) {
        return accountService.createAccount(request, clientId);
    }

    /**
     * Возвращает ответ с отображением списка счетов пользователя
     *
     * @param clientId уникальный идентификатор клиента
     * @return список транзакций
     */
    @GetMapping("/auth/accounts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение списка счетов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счета пользователя успешно получены"),
            @ApiResponse(responseCode = "401", description = "Отсутствует авторизация. Доступ запрещен",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Доступные счета не найдены",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))})})
    public List<ClientAccountsProjection> getListOfAccounts(@RequestHeader UUID clientId) {
        return accountService.getListOfAccounts(clientId);
    }

    @GetMapping("/auth/accounts/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Просмотр информации о счёте")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Реквизиты счёта"),
            @ApiResponse(responseCode = "401", description = "Отсутствует авторизация. Доступ запрещен",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Счёт не найден",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    public AccountInformationResponseDto viewAccountInformation(@PathVariable UUID accountId) {
        return accountService.viewAccountInformation(accountId);
    }

    /**
     * Возвращает ответ с отображением истории операций по счёту клиента
     *
     * @param accountId идентификатор счета из заголовка Header
     * @return список транзакций
     */
    @GetMapping("/auth/accounts/transactions")
    @Operation(summary = "Просмотр операций по счету")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список операций по счету"),
            @ApiResponse(responseCode = "401", description = "Отсутствует авторизация. Доступ запрещен",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Счёт не найден",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    public List<AccountTransactionResponseDto> getAccountTransactionsReport(@RequestHeader UUID accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

}
