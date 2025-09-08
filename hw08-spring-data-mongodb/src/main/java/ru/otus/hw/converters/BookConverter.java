package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;

@Component
public class BookConverter {

    public String bookToString(Book book) {
        return "Id: %s, title: %s, authorId: %s, genreId: %s".formatted(
                book.getId(),
                book.getTitle(),
                book.getAuthorId(),
                book.getGenreId());
    }
}
