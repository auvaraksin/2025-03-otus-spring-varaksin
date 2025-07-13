package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Application Command")
@RequiredArgsConstructor
public class ApplicationCommand {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Run command", key = {"r", "run", "s", "start"})
    public void run() {
        testRunnerService.run();
    }
}
