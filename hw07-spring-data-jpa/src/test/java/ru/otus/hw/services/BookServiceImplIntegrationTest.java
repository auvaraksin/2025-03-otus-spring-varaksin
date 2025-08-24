package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Import(BookServiceImpl.class)
public class BookServiceImplIntegrationTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("должен загружать книгу по id с инициализированными связями")
    void shouldFindByIdWithInitializedRelations() {
        Book book = bookService.findById(1L).orElseThrow();

        assertThat(book.getAuthor()).isNotNull();
        assertThat(book.getAuthor().getFullName()).isEqualTo("Author_1");

        assertThat(book.getGenre()).isNotNull();
        assertThat(book.getGenre().getName()).isEqualTo("Genre_1");
    }

    @Test
    @DisplayName("должен загружать список всех книг с инициализированными связями")
    void shouldFindAllWithInitializedRelations() {
        List<Book> books = bookService.findAll();

        assertThat(books).hasSize(3);
        books.forEach(book -> {
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getGenre()).isNotNull();
            assertThat(book.getAuthor().getFullName()).isEqualTo("Author_" + book.getId());
            assertThat(book.getGenre().getName()).isEqualTo("Genre_" + book.getId());
        });
    }

    @Test
    @DisplayName("должен сохранять новую книгу")
    void shouldInsertNewBook() {
        Book savedBook = bookService.insert("New Book", 1L, 1L);

        assertThat(savedBook.getId()).isPositive();
        assertThat(savedBook.getTitle()).isEqualTo("New Book");
        assertThat(savedBook.getAuthor().getId()).isEqualTo(1L);
        assertThat(savedBook.getGenre().getId()).isEqualTo(1L);

        Book foundBook = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertThat(foundBook).usingRecursiveComparison().isEqualTo(savedBook);
    }

    @Test
    @DisplayName("должен обновлять существующую книгу")
    void shouldUpdateExistingBook() {
        Book updatedBook = bookService.update(1L, "Updated Title", 2L, 2L);

        assertThat(updatedBook.getId()).isEqualTo(1L);
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(2L);
        assertThat(updatedBook.getGenre().getId()).isEqualTo(2L);

        Book foundBook = bookRepository.findById(1L).orElseThrow();
        assertThat(foundBook).usingRecursiveComparison().isEqualTo(updatedBook);
    }

    @Test
    @DisplayName("должен выбрасывать исключение при обновлении несуществующей книги")
    void shouldThrowWhenUpdateNonExistingBook() {
        assertThatThrownBy(() -> bookService.update(99L, "Title", 1L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id 99 not found");
    }

    @Test
    @DisplayName("должен удалять книгу по id")
    void shouldDeleteById() {
        assertThat(bookRepository.findById(1L)).isPresent();
        bookService.deleteById(1L);
        assertThat(bookRepository.findById(1L)).isEmpty();
    }
}
