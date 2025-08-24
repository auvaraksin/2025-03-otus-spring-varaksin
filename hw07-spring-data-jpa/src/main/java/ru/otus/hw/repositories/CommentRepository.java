package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.book WHERE c.id = :id")
    Optional<Comment> findById(@Param("id") long id);

    @Query("SELECT c FROM Comment c JOIN FETCH c.book WHERE c.book.id = :bookId")
    List<Comment> findAllByBookId(@Param("bookId") long bookId);
}
