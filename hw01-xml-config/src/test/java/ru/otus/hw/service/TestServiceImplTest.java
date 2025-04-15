package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.CsvQuestionDao;


import static org.mockito.Mockito.*;

public class TestServiceImplTest {

    private IOService ioService;
    private QuestionOutputViewService questionOutputViewService;
    private CsvQuestionDao questionDao;
    private TestServiceImpl testService;


    @BeforeEach
    void setUp() {
        ioService = mock(StreamsIOService.class);
        questionOutputViewService = mock(QuestionConsoleOutput.class);
        questionDao = mock(CsvQuestionDao.class);
        testService = new TestServiceImpl(ioService, questionOutputViewService, questionDao);
    }

    @Test
    @DisplayName("Should invoke one time execute test method")
    void shouldInvokeOneTimeExecuteTestMethod() {
        testService.executeTest();
        verify(questionDao, times(1)).findAll();
    }
}
