package ru.otus.projectwork.user.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.password-encoder")
public class PasswordEncoderConfig {

    private Integer strength = 15;

    private BCryptVersion version = BCryptVersion.$2A;

    public enum BCryptVersion {
        $2A(BCryptPasswordEncoder.BCryptVersion.$2A),
        $2B(BCryptPasswordEncoder.BCryptVersion.$2B),
        $2Y(BCryptPasswordEncoder.BCryptVersion.$2Y);

        private final BCryptPasswordEncoder.BCryptVersion version;

        BCryptVersion(BCryptPasswordEncoder.BCryptVersion version) {
            this.version = version;
        }

        public BCryptPasswordEncoder.BCryptVersion getVersion() {
            return version;
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(version.getVersion(), strength);
    }
}
