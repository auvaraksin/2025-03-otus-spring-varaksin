package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.CsvQuestionDao;



public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionOutputViewService questionOutputViewService;

    @Mock
    private CsvQuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should invoke one time execute test method")
    void shouldInvokeOneTimeExecuteTestMethod() {
        testService.executeTest();
        Mockito.verify(questionDao, Mockito.times(1)).findAll();
    }
}
