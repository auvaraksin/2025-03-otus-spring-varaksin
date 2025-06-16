package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<Question> questionList = new ArrayList<>();
        List<QuestionDto> questionDtoList;
        try {
            var is = getFileFromResourceAsStream(fileNameProvider.getTestFileName());
            var reader = new InputStreamReader(is);
            questionDtoList = new CsvToBeanBuilder(reader)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .withType(QuestionDto.class)
                    .build()
                    .parse();
        } catch (IllegalArgumentException e) {
            throw new QuestionReadException("<=====File " + fileNameProvider.getTestFileName() + " not found=====>");
        }

        for (QuestionDto questionDto : questionDtoList) {
            questionList.add(questionDto.toDomainObject());
        }

        return questionList;
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}
