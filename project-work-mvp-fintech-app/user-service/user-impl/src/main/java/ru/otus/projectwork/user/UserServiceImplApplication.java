package ru.otus.projectwork.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("ru.otus.projectwork.domain.model")
@EnableJpaRepositories(basePackages = "ru.otus.projectwork.domain.repository")
public class UserServiceImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceImplApplication.class, args);
    }

}
