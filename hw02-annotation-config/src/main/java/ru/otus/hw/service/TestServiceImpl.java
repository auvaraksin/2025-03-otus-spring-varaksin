package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {

            if (question.answers().isEmpty()) {
                ioService.printLine("Question '" + question.text() + "' has no answers - skipped");
                continue;
            }

            printQuestionWithPossibleAnswers(question);
            var answer = ioService.readIntForRangeWithPrompt(
                    1,
                    question.answers().size(),
                    "Введите номер варианта ответа",
                    "Введен некорректный вариант ответа. Введите число в диапазоне от 1 до " + question.answers().size()
            );
            var isAnswerValid = question.answers().get(answer - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printQuestionWithPossibleAnswers(Question question) {
        ioService.printLine(question.text());
        for (var answer : question.answers()) {
            ioService.printLine((question.answers().indexOf(answer) + 1) + " " + answer.text());
        }
    }
}
