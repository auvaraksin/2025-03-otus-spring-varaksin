package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questions = questionDao.findAll();

        printQuestionList(questions);
    }

    private void printQuestionList(List<Question> questionList) {
        for (Question question: questionList) {
            System.out.println("<=====Question=====>");
            System.out.println(question.text());
            System.out.println("<-----Answers----->");
            question.answers().forEach(System.out::println);
            System.out.println("<-----End of question----->");
            System.out.println();
        }
    }
}
