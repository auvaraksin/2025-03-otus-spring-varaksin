package ru.otus.projectwork.user.service.chain.authorization.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.projectwork.user.configuration.JwtConfig;
import ru.otus.projectwork.user.dto.ChainAuthorizationDto;
import ru.otus.projectwork.user.exception.GenerateJwtTokenError;
import ru.otus.projectwork.user.service.chain.authorization.ProcessAuthorizationChainPart;
import ru.otus.projectwork.user.util.token.JwtTokenUtils;

import java.time.Duration;
import java.util.UUID;

import static ru.otus.projectwork.user.service.chain.authorization.impl.GettingTokensChainPart.ORDER;
import static ru.otus.projectwork.user.util.ExceptionMessage.COULD_NOT_GENERATE_TOKENS;

/**
 * Часть цепочки для получения токенов доступа и обновления.
 */
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class GettingTokensChainPart implements ProcessAuthorizationChainPart {

    protected static final int ORDER = 4;

    private final JwtTokenUtils jwtTokenUtils;

    private final JwtConfig JWTConfig;

    /**
     * Выполняет получение токенов доступа и обновления для авторизованного клиента
     *
     * @param chain объект ChainAuthorizationDto, содержащий данные для обработки
     * @return ChainAuthorizationDto после выполнения поиска информации о клинте
     * @throws GenerateJwtTokenError в случае ошибки при получении токенов
     */
    @Override
    public ChainAuthorizationDto process(ChainAuthorizationDto chain) {
        String userFullName = chain.getUserFullName();
        UUID clientId = chain.getClientId();

        try {
            var accessTokenLifeTime = parseDuration(JWTConfig.getTime().getAccess());
            var refreshTokenLifeTime = parseDuration(JWTConfig.getTime().getRefresh());
            chain.setAccessToken(jwtTokenUtils.generateToken(clientId, userFullName, accessTokenLifeTime));
            chain.setRefreshToken(jwtTokenUtils.generateToken(clientId, userFullName, refreshTokenLifeTime));
        } catch (Exception e) {
            throw new GenerateJwtTokenError(COULD_NOT_GENERATE_TOKENS.getDescription());
        }

        return chain;
    }

    /**
     * Парсит Duration из строки (поддержка форматов 1d, 30m и т.д.)
     */
    private Duration parseDuration(String durationStr) {
        if (durationStr.endsWith("d")) {
            long days = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
            return Duration.ofDays(days);
        } else if (durationStr.endsWith("h")) {
            long hours = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
            return Duration.ofHours(hours);
        } else if (durationStr.endsWith("m")) {
            long minutes = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
            return Duration.ofMinutes(minutes);
        } else if (durationStr.endsWith("s")) {
            long seconds = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
            return Duration.ofSeconds(seconds);
        } else {
            return Duration.parse(durationStr); // Стандартный формат ISO-8601
        }
    }

}
