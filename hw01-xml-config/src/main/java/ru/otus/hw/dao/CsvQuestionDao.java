package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {

        List<QuestionDto> questionDtoList;
        List<Question> questionList = new ArrayList<>();
        try {
            File file = getFileFromResource(fileNameProvider);
            FileReader fileReader = new FileReader(file);
            questionDtoList = new CsvToBeanBuilder(fileReader)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .withType(QuestionDto.class)
                    .build()
                    .parse();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new QuestionReadException("<=====File " + fileNameProvider.getTestFileName() + " not found=====>");
        }

        for (QuestionDto questionDto: questionDtoList) {
            questionList.add(questionDto.toDomainObject());
        }

        return questionList;
    }

    private File getFileFromResource(TestFileNameProvider fileNameProvider) throws URISyntaxException {

        String fileName = fileNameProvider.getTestFileName();
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("<=====File " + fileName + " not found=====>");
        } else {
            return new File(resource.toURI());
        }
    }
}
