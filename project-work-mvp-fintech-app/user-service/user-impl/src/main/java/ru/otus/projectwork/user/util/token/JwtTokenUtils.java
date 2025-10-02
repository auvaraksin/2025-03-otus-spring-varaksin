package ru.otus.projectwork.user.util.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.otus.projectwork.user.configuration.JwtConfig;
import ru.otus.projectwork.user.exception.UnauthorizedUserException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static ru.otus.projectwork.user.util.ExceptionMessage.DENIED_ACCESS;
import static ru.otus.projectwork.user.util.ExceptionMessage.UNAUTHORIZED_USER;

/**
 * Утилитный класс для работы с JWT токенами.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtils {

    private final JwtConfig jwtConfig;

    private SecretKey key;

    @PostConstruct
    private void init() {
        try {
            log.info("Starting JWT initialization...");

            String secretKey = jwtConfig.getSecret().getKey();

            if (secretKey == null || secretKey.trim().isEmpty()) {
                throw new IllegalStateException("JWT secret key is not configured");
            }

            log.info("JWT config loaded successfully, key present: {}",
                    secretKey.length() > 10 ? "***" + secretKey.substring(secretKey.length() - 5) : "SHORT_KEY");

            byte[] keyBytes = Base64.getDecoder().decode(secretKey);

            // Проверка длины ключа
            if (keyBytes.length < 32) {
                log.warn("JWT key is too short ({} bytes), generating secure key", keyBytes.length);
                // Генерация безопасного ключа
                this.key = Jwts.SIG.HS256.key().build();
                log.info("Using auto-generated secure JWT key");
            } else {
                this.key = Keys.hmacShaKeyFor(keyBytes);
                log.info("JWT initialized successfully with configured key ({} bytes)", keyBytes.length);
            }

        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 format for JWT secret key", e);
            // Генерация безопасного ключа как fallback
            this.key = Jwts.SIG.HS256.key().build();
            log.warn("Using auto-generated JWT key due to configuration error");
        } catch (Exception e) {
            log.error("JWT initialization failed, using secure fallback", e);
            this.key = Jwts.SIG.HS256.key().build();
            log.warn("Using auto-generated secure JWT key");
        }
    }

    /**
     * Генерирует JWT токен для указанного пользователя.
     *
     * @param id идентификатор пользователя
     * @param userFullName полное имя пользователя
     * @param lifeTime время жизни токена
     * @return String сгенерированный JWT токен
     */
    public String generateToken(UUID id, String userFullName, Duration lifeTime) {

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifeTime.toMillis());

        Map<String, Object> claims = new HashMap<>();
        claims.put("clientId", id.toString());
        claims.put("userFullName", userFullName);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(key)
                .compact();
    }

    /**
     * Проверяет токен на валидность
     *
     * @param token токен
     * @return UUID возвращает идентификатор пользователя если токен валиден
     */
    public UUID tokenValidate(String token) {
        try {
            final String id = extractClientId(token);

            // Дополнительная проверка срока действия токена
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new UnauthorizedUserException("Token expired");
            }

            return getUuid(id);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            throw new UnauthorizedUserException(DENIED_ACCESS.getDescription());
        }
    }

    /**
     * Проверяет истек ли срок действия токена
     *
     * @param token токен
     * @return true если токен истек, false если действителен
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true; // Если не можем распарсить, считаем токен невалидным
        }
    }

    /**
     * Извлекает все данные из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Извлекает данные из токена
     *
     * @param token токен
     * @param claimsResolver функция извлечения данных
     * @param <T> тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            log.error("Invalid JWT token", e);
            throw new UnauthorizedUserException(DENIED_ACCESS.getDescription());
        }
    }

    /**
     * Извлекает clientId из токена
     *
     * @param jwtToken токен
     * @return UUID клиента в виде строки
     */
    public String extractClientId(String jwtToken) {
        return extractClaim(jwtToken, claims -> claims.get("clientId").toString());
    }

    /**
     * Извлекает полное имя пользователя из токена
     *
     * @param jwtToken токен
     * @return полное имя пользователя
     */
    public String extractUserFullName(String jwtToken) {
        return extractClaim(jwtToken, claims -> claims.get("userFullName").toString());
    }

    /**
     * Извлекает UUID из строкового представления с обработкой исключений. На случай если идентификатор пользователя не
     * соответствует формату UUID.
     *
     * @param extractedClientId строковое представление идентификатора клиента
     * @return UUID идентификатор клиента
     */
    private static UUID getUuid(String extractedClientId) {
        try {
            return UUID.fromString(extractedClientId);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedUserException(DENIED_ACCESS.getDescription());
        }
    }

    /**
     * Извлекает токен из заголовка Authorization или из cookies
     */
    public String getToken() {
        try {
            var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            // Сначала пробует получить из заголовка Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.debug("Token extracted from Authorization header");
                return token;
            }

            // Если нет в заголовке, пробует из cookies
            var cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                String cookiesName = jwtConfig.getCookies().getName();
                String token = Arrays.stream(cookies)
                        .filter(cook -> cook.getName().equals(cookiesName))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);

                if (token != null) {
                    log.debug("Token extracted from cookies");
                    return token;
                }
            }

            throw new UnauthorizedUserException(UNAUTHORIZED_USER.getDescription());

        } catch (IllegalStateException e) {
            // Если нет активного request context (например, в тестах)
            log.warn("No active request context available");
            throw new UnauthorizedUserException(UNAUTHORIZED_USER.getDescription());
        }
    }

    /**
     * Получает clientId из текущего запроса (удобный метод для контроллеров)
     */
    public UUID getCurrentClientId() {
        String token = getToken();
        return tokenValidate(token);
    }

    /**
     * Получает полное имя пользователя из текущего запроса
     */
    public String getCurrentUserFullName() {
        String token = getToken();
        return extractUserFullName(token);
    }
}