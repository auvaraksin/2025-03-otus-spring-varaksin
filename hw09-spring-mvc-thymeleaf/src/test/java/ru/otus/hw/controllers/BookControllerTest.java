package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.conrollers.BookController;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
@DisplayName("Контроллер для работы с книгами")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    private final Author author = new Author(1L, "Test Author");
    private final Genre genre = new Genre(1L, "Test Genre");
    private final Book book = new Book(1L, "Test Book", author, genre);

    @Test
    @DisplayName("должен загружать страницу со списком всех книг")
    void listBooks_ShouldReturnBooksListPage() throws Exception {
        given(bookService.findAll()).willReturn(List.of(book));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of(book)));

        verify(bookService, times(1)).findAll();
    }

    @Test
    @DisplayName("должен загружать страницу просмотра книги когда книга существует")
    void viewBook_WhenBookExists_ShouldReturnViewPage() throws Exception {
        given(bookService.findById(1L)).willReturn(Optional.of(book));
        given(commentService.findAllByBookId(1L)).willReturn(List.of());

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/view"))
                .andExpect(model().attributeExists("book", "comments"))
                .andExpect(model().attribute("book", book));

        verify(bookService, times(1)).findById(1L);
        verify(commentService, times(1)).findAllByBookId(1L);
    }

    @Test
    @DisplayName("должен возвращать статус 404 когда книга не существует")
    void viewBook_WhenBookNotExists_ShouldThrowException() throws Exception {
        given(bookService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("должен загружать страницу создания новой книги")
    void createBookForm_ShouldReturnFormPageWithEmptyBook() throws Exception {
        given(authorService.findAll()).willReturn(List.of(author));
        given(genreService.findAll()).willReturn(List.of(genre));

        mockMvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeExists("book", "authors", "genres"))
                .andExpect(model().attribute("authors", List.of(author)))
                .andExpect(model().attribute("genres", List.of(genre)));

        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("должен загружать страницу редактирования книги когда книга существует")
    void editBookForm_WhenBookExists_ShouldReturnFormPageWithBook() throws Exception {
        given(bookService.findById(1L)).willReturn(Optional.of(book));
        given(authorService.findAll()).willReturn(List.of(author));
        given(genreService.findAll()).willReturn(List.of(genre));

        mockMvc.perform(get("/books/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeExists("book", "authors", "genres"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("authors", List.of(author)))
                .andExpect(model().attribute("genres", List.of(genre)));

        verify(bookService, times(1)).findById(1L);
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("должен возвращать статус 404 при попытке редактирования несуществующей книги")
    void editBookForm_WhenBookNotExists_ShouldThrowException() throws Exception {
        given(bookService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/books/edit/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("должен сохранять новую книгу и выполнять редирект")
    void saveBook_WhenNewBook_ShouldInsertAndRedirect() throws Exception {
        mockMvc.perform(post("/books/save")
                        .param("id", "0")
                        .param("title", "New Book")
                        .param("authorId", "1")
                        .param("genreId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1)).insert("New Book", 1L, 1L);
    }

    @Test
    @DisplayName("должен обновлять существующую книгу и выполнять редирект")
    void saveBook_WhenExistingBook_ShouldUpdateAndRedirect() throws Exception {
        mockMvc.perform(post("/books/save")
                        .param("id", "1")
                        .param("title", "Updated Book")
                        .param("authorId", "1")
                        .param("genreId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1)).update(1L, "Updated Book", 1L, 1L);
    }

    @Test
    @DisplayName("должен загружать страницу подтверждения удаления когда книга существует")
    void deleteBookForm_WhenBookExists_ShouldReturnDeletePage() throws Exception {
        given(bookService.findById(1L)).willReturn(Optional.of(book));

        mockMvc.perform(get("/books/delete/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/delete"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", book));

        verify(bookService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("должен возвращать статус 404 при попытке удаления несуществующей книги")
    void deleteBookForm_WhenBookNotExists_ShouldThrowException() throws Exception {
        given(bookService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/books/delete/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("должен удалять книгу и выполнять редирект")
    void deleteBook_ShouldDeleteAndRedirect() throws Exception {
        mockMvc.perform(post("/books/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1)).deleteById(1L);
    }
}
