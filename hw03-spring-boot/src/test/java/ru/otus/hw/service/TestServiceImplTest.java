package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestServiceImplTest {

    @Mock
    private LocalizedIOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    private final Student testStudent = new Student("Test", "Student");
    private final Question questionWithAnswers = new Question("Q1", List.of(
            new Answer("A1", true),
            new Answer("A2", false)
    ));
    private final Question emptyQuestion = new Question("Empty", List.of());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Должен корректно выполнять тест и возвращать результат")
    void shouldReturnTestResult() {
        when(questionDao.findAll()).thenReturn(List.of(questionWithAnswers));
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString(), anyInt()))
                .thenReturn(1);

        var result = testService.executeTestFor(testStudent);

        assertNotNull(result);
        assertEquals(testStudent, result.getStudent());
        assertEquals(1, result.getAnsweredQuestions().size());
        assertEquals(1, result.getRightAnswersCount());
        verify(ioService, atLeastOnce()).printLine(anyString());
    }

    @Test
    @DisplayName("Должен корректно пропускать вопросы без ответов")
    void shouldHandleEmptyQuestions() {
        when(questionDao.findAll()).thenReturn(List.of(emptyQuestion));

        var result = testService.executeTestFor(testStudent);

        assertEquals(0, result.getAnsweredQuestions().size());
        assertEquals(0, result.getRightAnswersCount());
    }

    @Test
    @DisplayName("Должен корректно форматировать вопрос с вариантами ответов")
    void shouldCorrectlyPrintQuestionWithAnswers() {
        when(questionDao.findAll()).thenReturn(List.of(questionWithAnswers));
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString(), anyInt()))
                .thenReturn(1);

        testService.executeTestFor(testStudent);

        verify(ioService).printLine("Q1");
        verify(ioService).printLine(startsWith("1"));
        verify(ioService).printLine(startsWith("2"));
    }
}
