package ru.otus.hw.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.TestRunnerService;

@Component
@ConditionalOnProperty(
        name = "spring.shell.interactive.enabled",
        havingValue = "false",
        matchIfMissing = false
)
public class AppRunner implements ApplicationRunner {

    private final TestRunnerService testRunnerService;

    public AppRunner(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }

    @Override
    public void run(ApplicationArguments args) {
        testRunnerService.run();
    }
}
