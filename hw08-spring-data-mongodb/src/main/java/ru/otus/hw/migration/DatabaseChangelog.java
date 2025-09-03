package ru.otus.hw.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "auvaraksin", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "system")
    public void insertAuthors(AuthorRepository authorRepository) {
        Author author1 = authorRepository.save(new Author(null, "Author_1"));
        Author author2 = authorRepository.save(new Author(null, "Author_2"));
        Author author3 = authorRepository.save(new Author(null, "Author_3"));
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "system")
    public void insertGenres(GenreRepository genreRepository) {
        Genre genre1 = genreRepository.save(new Genre(null, "Genre_1"));
        Genre genre2 = genreRepository.save(new Genre(null, "Genre_2"));
        Genre genre3 = genreRepository.save(new Genre(null, "Genre_3"));
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "system")
    public void insertBooks(BookRepository bookRepository, AuthorRepository authorRepository, GenreRepository genreRepository) {
        Author author1 = authorRepository.findAll().get(0);
        Author author2 = authorRepository.findAll().get(1);
        Author author3 = authorRepository.findAll().get(2);

        Genre genre1 = genreRepository.findAll().get(0);
        Genre genre2 = genreRepository.findAll().get(1);
        Genre genre3 = genreRepository.findAll().get(2);

        Book book1 = bookRepository.save(new Book(null, "BookTitle_1", author1.getId(), genre1.getId()));
        Book book2 = bookRepository.save(new Book(null, "BookTitle_2", author2.getId(), genre2.getId()));
        Book book3 = bookRepository.save(new Book(null, "BookTitle_3", author3.getId(), genre3.getId()));
    }

    @ChangeSet(order = "005", id = "insertComments", author = "system")
    public void insertComments(CommentRepository commentRepository, BookRepository bookRepository) {
        Book book1 = bookRepository.findAll().get(0);
        Book book2 = bookRepository.findAll().get(1);

        commentRepository.save(new Comment(null, "Comment 1 for book 1", book1.getId()));
        commentRepository.save(new Comment(null, "Comment 2 for book 1", book1.getId()));
        commentRepository.save(new Comment(null, "Comment for book 2", book2.getId()));
    }
}
