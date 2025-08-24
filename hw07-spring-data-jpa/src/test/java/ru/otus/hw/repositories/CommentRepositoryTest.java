package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с комментариями")
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private TestEntityManager em;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbComments = getDbComments();
    }

    @DisplayName("должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource("getDbComments")
    void shouldReturnCorrectCommentById(Comment expectedComment) {
        var actualComment = repository.findById(expectedComment.getId());
        var emComment = em.find(Comment.class, expectedComment.getId());

        assertThat(actualComment)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(emComment);
    }

    @DisplayName("должен загружать список всех комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentsListByBookId() {
        var actualComments = repository.findAllByBookId(1L);

        var expectedComment1 = em.find(Comment.class, 1L);
        var expectedComment2 = em.find(Comment.class, 2L);
        var expectedComments = List.of(expectedComment1, expectedComment2);

        assertThat(actualComments)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedComments);

        assertThat(actualComments)
                .allMatch(comment -> em.find(Comment.class, comment.getId()) != null);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = em.find(Book.class, 1L);
        var expectedComment = new Comment(0, "New Comment", book);

        var returnedComment = repository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(repository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять измененный комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var book = em.find(Book.class, 2L);
        var expectedComment = new Comment(1L, "Updated Comment", book);

        assertThat(repository.findById(expectedComment.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedComment);

        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(repository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        assertThat(repository.findById(1L)).isPresent();
        repository.deleteById(1L);
        assertThat(repository.findById(1L)).isEmpty();
    }

    private static List<Comment> getDbComments() {
        return List.of(
                new Comment(1L, "Comment 1 for book 1",
                        new Book(1L, "BookTitle_1",
                                new Author(1L, "Author_1"),
                                new Genre(1L, "Genre_1"))),
                new Comment(2L, "Comment 2 for book 1",
                        new Book(1L, "BookTitle_1",
                                new Author(1L, "Author_1"),
                                new Genre(1L, "Genre_1"))),
                new Comment(3L, "Comment for book 2",
                        new Book(2L, "BookTitle_2",
                                new Author(2L, "Author_2"),
                                new Genre(2L, "Genre_2")))
        );
    }
}
