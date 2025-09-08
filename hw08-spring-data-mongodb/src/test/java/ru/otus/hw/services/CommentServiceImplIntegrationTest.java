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
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с комментариями")
@DataMongoTest
@Import({CommentServiceImpl.class, EmbeddedMongoConfig.class, TestMongoConfig.class})
@TestPropertySource(properties = {
        "de.flapdoodle.mongodb.embedded.version=4.0.2"})
public class CommentServiceImplIntegrationTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    private String existingBookId;
    private String existingCommentId;

    @BeforeEach
    void setUp() {
        Optional<Comment> firstComment = commentRepository.findAll().stream().findFirst();
        existingCommentId = firstComment.map(Comment::getId).orElse(null);
        existingBookId = firstComment.map(Comment::getBookId).orElse(null);
    }

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldFindById() {
        Comment comment = commentService.findById(existingCommentId).orElseThrow();

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isNotNull();
        assertThat(comment.getBookId()).isNotNull();
    }

    @Test
    @DisplayName("должен загружать список комментариев по id книги")
    void shouldFindAllByBookId() {
        List<Comment> comments = commentService.findAllByBookId(existingBookId);

        assertThat(comments).isNotEmpty();
        comments.forEach(comment -> {
            assertThat(comment.getText()).isNotNull();
            assertThat(comment.getBookId()).isEqualTo(existingBookId);
        });
    }

    @Test
    @DisplayName("должен сохранять новый комментарий")
    void shouldSaveNewComment() {
        Comment savedComment = commentService.save(existingBookId, "New Comment");

        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("New Comment");
        assertThat(savedComment.getBookId()).isEqualTo(existingBookId);

        Comment foundComment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertThat(foundComment).usingRecursiveComparison().isEqualTo(savedComment);
    }

    @Test
    @DisplayName("должен выбрасывать исключение при сохранении комментария для несуществующей книги")
    void shouldThrowWhenSaveCommentForNonExistingBook() {
        assertThatThrownBy(() -> commentService.save("non-existing-book-id", "Comment"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id non-existing-book-id not found");
    }

    @Test
    @DisplayName("должен удалять комментарий по id")
    void shouldDeleteById() {
        assertThat(commentRepository.findById(existingCommentId)).isPresent();
        commentService.deleteById(existingCommentId);
        assertThat(commentRepository.findById(existingCommentId)).isEmpty();
    }
}
