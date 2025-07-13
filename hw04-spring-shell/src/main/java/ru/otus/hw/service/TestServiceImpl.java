package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        printTestHeader();

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            if (question.answers().isEmpty()) {
                ioService.printFormattedLineLocalized("TestService.answer.error.question.no_answers",
                        question.text());
                continue;
            }

            printQuestionWithPossibleAnswers(question);
            var answer = ioService.readIntForRangeWithPromptLocalized(
                    1, question.answers().size(),
                    "TestService.enter.answer.number",
                    "TestService.answer.number.error",
                    question.answers().size()
            );
            var isAnswerValid = question.answers().get(answer - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printTestHeader() {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");
    }

    private void printQuestionWithPossibleAnswers(Question question) {
        ioService.printLine(question.text());
        for (var answer : question.answers()) {
            ioService.printLine((question.answers().indexOf(answer) + 1) + " " + answer.text());
        }
    }
}
