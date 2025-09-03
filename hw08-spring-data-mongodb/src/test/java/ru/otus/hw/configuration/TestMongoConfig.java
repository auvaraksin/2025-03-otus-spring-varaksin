package ru.otus.hw.configuration;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@TestConfiguration
@EnableMongock
@EnableMongoRepositories
public class TestMongoConfig {
}
