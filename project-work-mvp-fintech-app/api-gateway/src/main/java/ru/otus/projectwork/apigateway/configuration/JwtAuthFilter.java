package ru.otus.projectwork.apigateway.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Фильтр аутентификации JWT для Spring Cloud Gateway.
 *
 * <p>Этот фильтр выполняет следующие функции:
 * <ul>
 *   <li>Проверяет JWT токены в заголовках Authorization или cookies</li>
 *   <li>Пропускает публичные эндпоинты без аутентификации</li>
 *   <li>Извлекает clientId из валидного JWT токена</li>
 *   <li>Добавляет заголовок X-User-Id с clientId для downstream сервисов</li>
 *   <li>Возвращает HTTP 401 для невалидных или отсутствующих токенов</li>
 * </ul>
 *
 * @see AbstractGatewayFilterFactory
 * @see GatewayFilter
 */
@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${jwt.secret.key}")
    private String secretKey;

    /**
     * Конфигурационный класс для JwtAuthFilter.
     *
     * <p>В текущей реализации конфигурация не содержит параметров,
     * но может быть расширена при необходимости.
     */
    public static class Config {
        // Конфигурация может быть пустой или содержать дополнительные параметры
    }

    /**
     * Конструктор по умолчанию.
     */
    public JwtAuthFilter() {
        super(Config.class);
    }

    /**
     * Проверяет конфигурацию фильтра после инициализации бина.
     *
     * @throws IllegalStateException если секретный ключ JWT не сконфигурирован
     */
    @PostConstruct
    public void validateConfig() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured");
        }
        log.info("✅ JwtAuthFilter initialized with secret key");
    }

    /**
     * Создает секретный ключ для верификации JWT токенов.
     *
     * @return SecretKey созданный из base64-закодированного секретного ключа
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Создает GatewayFilter для обработки JWT аутентификации.
     *
     * <p>Фильтр выполняет следующую последовательность действий:
     * <ol>
     *   <li>Проверяет, является ли путь публичным</li>
     *   <li>Извлекает JWT токен из заголовков или cookies</li>
     *   <li>Валидирует токен и его срок действия</li>
     *   <li>Извлекает clientId из токена и добавляет в заголовок X-User-Id</li>
     *   <li>Пропускает запрос дальше по цепочке фильтров или возвращает ошибку</li>
     * </ol>
     *
     * @param config конфигурация фильтра
     * @return GatewayFilter настроенный фильтр аутентификации
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            String method = exchange.getRequest().getMethod().name();

            log.debug("🔐 Checking authentication for {} {}", method, path);

            // Пропускает публичные пути
            if (isPublicPath(path)) {
                log.debug("✅ Public path, skipping authentication for {}", path);
                return chain.filter(exchange);
            }

            // Извлекает и проверяет токен
            String token = extractToken(exchange.getRequest());
            if (token == null) {
                log.warn("❌ No JWT token found in request for {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            if (validateToken(token)) {
                log.debug("✅ Token is valid, proceeding with request to {}", path);

                // Добавляет userId в headers для downstream сервисов
                String userId = getUserIdFromToken(token);
                if (userId != null) {
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .build();
                    exchange = exchange.mutate().request(mutatedRequest).build();
                    log.debug("👤 Added X-User-Id header: {}", userId);
                } else {
                    log.warn("⚠️ userId is null, cannot add X-User-Id header");
                    try {
                        Claims claims = Jwts.parser()
                                .verifyWith(getSigningKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
                        log.debug("🔍 All token claims: {}", claims);
                    } catch (Exception e) {
                        log.error("🚨 Failed to parse token for debugging: {}", e.getMessage());
                    }
                }

                return chain.filter(exchange);
            } else {
                log.warn("❌ Invalid JWT token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    /**
     * Валидирует JWT токен.
     *
     * <p>Проверяет:
     * <ul>
     *   <li>Подпись токена с использованием секретного ключа</li>
     *   <li>Срок действия токена (expiration)</li>
     * </ul>
     *
     * @param token JWT токен для валидации
     * @return true если токен валиден, false в противном случае
     */
    private boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getExpiration().before(new Date())) {
                log.warn("⏰ Token expired at: {}", claims.getExpiration());
                return false;
            }

            log.debug("🔍 Token validated for user: {}, expires: {}",
                    claims.getSubject(), claims.getExpiration());
            return true;
        } catch (Exception e) {
            log.error("🚨 Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Извлекает идентификатор пользователя из JWT токена.
     *
     * <p>Пытается извлечь clientId в следующем порядке:
     * <ol>
     *   <li>Из claims по ключу "clientId"</li>
     *   <li>Из subject токена, если clientId не найден</li>
     * </ol>
     *
     * @param token JWT токен
     * @return clientId пользователя или null если не удалось извлечь
     */
    private String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Способ 1: Проверка через get() с обработкой null
            String clientId = claims.get("clientId", String.class);
            if (clientId != null) {
                log.debug("🔍 Extracted clientId from token: {}", clientId);
                return clientId;
            }

            // Способ 2: Через getSubject() если clientId в subject
            String subject = claims.getSubject();
            if (subject != null && !subject.isEmpty()) {
                log.debug("🔍 Using subject as userId: {}", subject);
                return subject;
            }

            log.warn("⚠️ Neither clientId nor subject found in token");
            return null;

        } catch (Exception e) {
            log.warn("⚠️ Could not extract clientId from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет, является ли путь публичным (не требующим аутентификации).
     *
     * <p>Публичные пути включают:
     * <ul>
     *   <li>API пользователей через Gateway: /api/users/public/**</li>
     *   <li>Прямые пути к User Service: /user-service/public/**</li>
     *   <li>Actuator endpoints: /actuator/health, /actuator/info, /actuator/user-service/**</li>
     * </ul>
     *
     * @param path путь запроса
     * @return true если путь публичный, false если требуется аутентификация
     */
    private boolean isPublicPath(String path) {
        boolean isPublic =
                // Публичные API через Gateway
                path.startsWith("/api/users/public/") ||
                        // Прямые пути к User Service
                        path.startsWith("/user-service/public/") ||
                        // Actuator endpoints
                        path.equals("/actuator/health") ||
                        path.equals("/actuator/info") ||
                        path.startsWith("/actuator/user-service/") ||
                        path.startsWith("/actuator/account-service/");

        log.debug("🛣️ Path: {} -> Public: {}", path, isPublic);
        return isPublic;
    }

    /**
     * Извлекает JWT токен из HTTP запроса.
     *
     * <p>Ищет токен в следующих местах (в порядке приоритета):
     * <ol>
     *   <li>Заголовок Authorization с префиксом "Bearer "</li>
     *   <li>Cookie с именем "accessToken"</li>
     * </ol>
     *
     * @param request HTTP запрос
     * @return JWT токен или null если токен не найден
     */
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("🔑 Extracted token from Authorization header");
            return token;
        }

        // Проверка cookies для использования cookie-based auth
        String cookieHeader = request.getHeaders().getFirst("Cookie");
        if (cookieHeader != null && cookieHeader.contains("accessToken=")) {
            String token = extractTokenFromCookie(cookieHeader);
            if (token != null) {
                log.debug("🍪 Extracted token from accessToken cookie");
                return token;
            }
        }

        log.debug("📭 No token found in headers or cookies");
        return null;
    }

    /**
     * Извлекает JWT токен из строки cookie.
     *
     * @param cookieHeader значение заголовка Cookie
     * @return JWT токен или null если токен не найден или произошла ошибка
     */
    private String extractTokenFromCookie(String cookieHeader) {
        try {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                if (cookie.trim().startsWith("accessToken=")) {
                    return cookie.split("=")[1].trim();
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Error extracting token from cookie: {}", e.getMessage());
        }
        return null;
    }
}
