package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с комментариями")
@DataJpaTest
@Import(CommentServiceImpl.class)
public class CommentServiceImplIntegrationTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("должен загружать комментарий по id с инициализированной связью с книгой")
    void shouldFindByIdWithInitializedBook() {
        Comment comment = commentService.findById(1L).orElseThrow();

        assertThat(comment.getBook()).isNotNull();
        assertThat(comment.getBook().getTitle()).isEqualTo("BookTitle_1");
    }

    @Test
    @DisplayName("должен загружать список комментариев по id книги с инициализированными связями")
    void shouldFindAllByBookIdWithInitializedRelations() {
        List<Comment> comments = commentService.findAllByBookId(1L);

        assertThat(comments).hasSize(2);
        comments.forEach(comment -> {
            assertThat(comment.getBook()).isNotNull();
            assertThat(comment.getBook().getId()).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("должен сохранять новый комментарий")
    void shouldSaveNewComment() {
        Comment savedComment = commentService.save(1L, "New Comment");

        assertThat(savedComment.getId()).isPositive();
        assertThat(savedComment.getText()).isEqualTo("New Comment");
        assertThat(savedComment.getBook().getId()).isEqualTo(1L);

        Comment foundComment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertThat(foundComment).usingRecursiveComparison().isEqualTo(savedComment);
    }

    @Test
    @DisplayName("должен выбрасывать исключение при сохранении комментария для несуществующей книги")
    void shouldThrowWhenSaveCommentForNonExistingBook() {
        assertThatThrownBy(() -> commentService.save(99L, "Comment"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id 99 not found");
    }

    @Test
    @DisplayName("должен удалять комментарий по id")
    void shouldDeleteById() {
        assertThat(commentRepository.findById(1L)).isPresent();
        commentService.deleteById(1L);
        assertThat(commentRepository.findById(1L)).isEmpty();
    }
}
