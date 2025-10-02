package ru.otus.projectwork.user.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
@Slf4j
public class JwtConfig {

    private Secret secret = new Secret();

    private Cookies cookies = new Cookies();

    private Time time = new Time();

    @PostConstruct
    public void validate() {
        if (secret == null || secret.getKey() == null || secret.getKey().trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured in application.yml");
        }
        if (cookies == null || cookies.getName() == null || cookies.getName().trim().isEmpty()) {
            throw new IllegalStateException("JWT cookie name is not configured in application.yml");
        }
        log.info("SecurityConfig validated successfully");
    }

    @Getter
    @Setter
    public static class Secret {
        private String key;
    }

    @Getter
    @Setter
    public static class Cookies {
        private String name;
    }

    @Getter
    @Setter
    public static class Time {
        private String refresh;
        private String access;
    }

}