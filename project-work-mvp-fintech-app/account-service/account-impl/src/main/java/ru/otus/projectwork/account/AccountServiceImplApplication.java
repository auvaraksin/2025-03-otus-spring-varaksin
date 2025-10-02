package ru.otus.projectwork.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("ru.otus.projectwork.domain.model")
@EnableJpaRepositories(basePackages = "ru.otus.projectwork.domain.repository")
@ConfigurationPropertiesScan
public class AccountServiceImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceImplApplication.class, args);
    }

}
