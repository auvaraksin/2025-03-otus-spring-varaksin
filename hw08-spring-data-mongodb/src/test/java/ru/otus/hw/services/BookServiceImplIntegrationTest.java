package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.configuration.EmbeddedMongoConfig;
import ru.otus.hw.configuration.TestMongoConfig;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с книгами")
@DataMongoTest
@Import({BookServiceImpl.class, EmbeddedMongoConfig.class, TestMongoConfig.class})
@TestPropertySource(properties = {
        "de.flapdoodle.mongodb.embedded.version=4.0.2"})
public class BookServiceImplIntegrationTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private String existingBookId;
    private String existingAuthorId;
    private String existingGenreId;

    @BeforeEach
    void setUp() {
        // Получаем ID из мигрированных данных
        Optional<Book> firstBook = bookRepository.findAll().stream().findFirst();
        existingBookId = firstBook.map(Book::getId).orElse(null);
        existingAuthorId = firstBook.map(Book::getAuthorId).orElse(null);
        existingGenreId = firstBook.map(Book::getGenreId).orElse(null);
    }

    @Test
    @DisplayName("должен загружать книгу по id")
    void shouldFindById() {
        Book book = bookService.findById(existingBookId).orElseThrow();

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isNotNull();
        assertThat(book.getAuthorId()).isNotNull();
        assertThat(book.getGenreId()).isNotNull();
    }

    @Test
    @DisplayName("должен загружать список всех книг")
    void shouldFindAll() {
        List<Book> books = bookService.findAll();

        assertThat(books).isNotEmpty();
        books.forEach(book -> {
            assertThat(book.getTitle()).isNotNull();
            assertThat(book.getAuthorId()).isNotNull();
            assertThat(book.getGenreId()).isNotNull();
        });
    }

    @Test
    @DisplayName("должен сохранять новую книгу")
    void shouldInsertNewBook() {
        Book savedBook = bookService.insert("New Book", existingAuthorId, existingGenreId);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("New Book");
        assertThat(savedBook.getAuthorId()).isEqualTo(existingAuthorId);
        assertThat(savedBook.getGenreId()).isEqualTo(existingGenreId);

        Book foundBook = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertThat(foundBook).usingRecursiveComparison().isEqualTo(savedBook);
    }

    @Test
    @DisplayName("должен обновлять существующую книгу")
    void shouldUpdateExistingBook() {
        Book updatedBook = bookService.update(existingBookId, "Updated Title", existingAuthorId, existingGenreId);

        assertThat(updatedBook.getId()).isEqualTo(existingBookId);
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getAuthorId()).isEqualTo(existingAuthorId);
        assertThat(updatedBook.getGenreId()).isEqualTo(existingGenreId);

        Book foundBook = bookRepository.findById(existingBookId).orElseThrow();
        assertThat(foundBook).usingRecursiveComparison().isEqualTo(updatedBook);
    }

    @Test
    @DisplayName("должен выбрасывать исключение при обновлении несуществующей книги")
    void shouldThrowWhenUpdateNonExistingBook() {
        assertThatThrownBy(() -> bookService.update("non-existing-id", "Title", existingAuthorId, existingGenreId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id non-existing-id not found");
    }

    @Test
    @DisplayName("должен удалять книгу по id")
    void shouldDeleteById() {
        assertThat(bookRepository.findById(existingBookId)).isPresent();
        bookService.deleteById(existingBookId);
        assertThat(bookRepository.findById(existingBookId)).isEmpty();
    }
}
