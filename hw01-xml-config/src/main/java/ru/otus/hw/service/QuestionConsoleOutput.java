package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

import java.util.List;

public class QuestionConsoleOutput implements QuestionOutputViewService {

    @Override
    public void printQuestionList(List<Question> questionList) {
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
