package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book insert(String title, String authorId, String genreId) {
        if (!authorRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Author with id %s not found".formatted(authorId));
        }
        if (!genreRepository.existsById(genreId)) {
            throw new EntityNotFoundException("Genre with id %s not found".formatted(genreId));
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthorId(authorId);
        book.setGenreId(genreId);

        return bookRepository.save(book);
    }

    @Override
    public Book update(String id, String title, String authorId, String genreId) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));

        if (!authorRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Author with id %s not found".formatted(authorId));
        }
        if (!genreRepository.existsById(genreId)) {
            throw new EntityNotFoundException("Genre with id %s not found".formatted(genreId));
        }

        existingBook.setTitle(title);
        existingBook.setAuthorId(authorId);
        existingBook.setGenreId(genreId);

        return bookRepository.save(existingBook);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }
}
