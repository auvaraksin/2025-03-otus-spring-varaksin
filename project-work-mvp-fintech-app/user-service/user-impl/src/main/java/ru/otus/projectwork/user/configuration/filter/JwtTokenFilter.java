package ru.otus.projectwork.user.configuration.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.otus.projectwork.user.util.token.JwtTokenUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestUri = request.getRequestURI();

        log.debug("=== JWT FILTER START ===");
        log.debug("Request URI: {}", requestUri);

        // Пропускает публичные эндпоинты без проверки токена
        if (requestUri.startsWith("/public/") ||
                requestUri.contains("/actuator/") ||
                requestUri.contains("/swagger-ui") ||
                requestUri.contains("/v3/api-docs")) {
            log.debug("Skipping JWT check for public endpoint: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // Для защищенных эндпоинтов проверяет токен
        if (requestUri.startsWith("/auth/")) {
            // Если уже есть аутентификация и пользователь аутентифицирован, пропускает проверку
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                log.debug("Authentication already present, skipping JWT check");
                filterChain.doFilter(request, response);
                return;
            }

            try {
                String token = jwtTokenUtils.getToken();
                log.debug("Extracted token: {}", token != null ? "PRESENT" : "NULL");

                if (token != null) {
                    UUID clientId = jwtTokenUtils.tokenValidate(token);
                    log.debug("Token validation result: {}", clientId);

                    if (clientId != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(clientId, null, Collections.emptyList());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Authenticated user with ID: {}", clientId);
                    } else {
                        log.debug("Token validation failed, sending 401");
                        sendUnauthorizedResponse(response, "Invalid token");
                        return;
                    }
                } else {
                    log.debug("No token found, sending 401");
                    sendUnauthorizedResponse(response, "Missing authentication token");
                    return;
                }
            } catch (Exception e) {
                log.debug("JWT authentication failed: {}", e.getMessage());
                sendUnauthorizedResponse(response, "Invalid or expired token: " + e.getMessage());
                return;
            }
        }

        log.debug("=== JWT FILTER END ===");
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }
}
