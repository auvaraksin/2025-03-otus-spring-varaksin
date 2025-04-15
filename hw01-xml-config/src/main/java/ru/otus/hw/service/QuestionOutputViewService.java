package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

import java.util.List;

public interface QuestionOutputViewService {
    void printQuestionList(List<Question> questionList);
}
