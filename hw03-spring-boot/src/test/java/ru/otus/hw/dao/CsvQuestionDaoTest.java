package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest {

    private TestFileNameProvider fileNameProvider;
    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    void setUp() {
        fileNameProvider = Mockito.mock(TestFileNameProvider.class);
        csvQuestionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    @DisplayName("Должен корректно загружать вопросы из CSV-файла")
    void shouldLoadQuestionsCorrectly() {

        String testFileName = "test-questions.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(testFileName);

        List<Question> questions = csvQuestionDao.findAll();

        assertNotNull(questions);
        assertEquals(2, questions.size());

        var firstQuestion = questions.get(0);
        assertEquals("Is this a test question?", firstQuestion.text());
        assertEquals(2, firstQuestion.answers().size());
        assertAnswer(firstQuestion.answers().get(0), "Yes", true);
        assertAnswer(firstQuestion.answers().get(1), "No", false);

        var secondQuestion = questions.get(1);
        assertEquals("Another test question?", secondQuestion.text());
        assertEquals(3, secondQuestion.answers().size());
        assertAnswer(secondQuestion.answers().get(0), "Option 1", false);
        assertAnswer(secondQuestion.answers().get(1), "Option 2", true);
        assertAnswer(secondQuestion.answers().get(2), "Option 3", false);
    }

    @Test
    @DisplayName("Должен выбрасывать исключение при отсутствии файла")
    void shouldThrowExceptionWhenFileNotFound() {

        String nonExistentFile = "non-existent.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(nonExistentFile);

        QuestionReadException exception = assertThrows(QuestionReadException.class, () -> {
            csvQuestionDao.findAll();
        });

        assertTrue(exception.getMessage().contains(nonExistentFile));
    }

    private void assertAnswer(Answer answer, String expectedText, boolean expectedIsCorrect) {
        assertEquals(expectedText, answer.text());
        assertEquals(expectedIsCorrect, answer.isCorrect());
    }
}
