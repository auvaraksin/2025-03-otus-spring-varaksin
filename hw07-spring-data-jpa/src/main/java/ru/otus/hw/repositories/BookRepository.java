package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genre WHERE b.id = :id")
    Optional<Book> findById(@Param("id") long id);

    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genre")
    List<Book> findAll();
}
