package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.conrollers.BookController;
import ru.otus.hw.dtos.AuthorDto;
import ru.otus.hw.dtos.BookDto;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@DisplayName("REST контроллер для работы с книгами")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private final Author author = new Author(1L, "Test Author");
    private final Genre genre = new Genre(1L, "Test Genre");
    private final Book book = new Book(1L, "Test Book", author, genre);
    private final BookDto bookDto = new BookDto(1L, "Test Book", new AuthorDto(author), new GenreDto(genre));

    @Test
    @DisplayName("должен возвращать список всех книг")
    void getAllBooks_ShouldReturnBooksList() throws Exception {
        given(bookService.findAllDto()).willReturn(List.of(bookDto));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author.id").value(1L))
                .andExpect(jsonPath("$[0].author.fullName").value("Test Author"))
                .andExpect(jsonPath("$[0].genre.id").value(1L))
                .andExpect(jsonPath("$[0].genre.name").value("Test Genre"));

        verify(bookService, times(1)).findAllDto();
    }

    @Test
    @DisplayName("должен возвращать книгу по ID когда книга существует")
    void getBook_WhenBookExists_ShouldReturnBook() throws Exception {
        given(bookService.findByIdDto(1L)).willReturn(Optional.of(bookDto));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author.id").value(1L))
                .andExpect(jsonPath("$.author.fullName").value("Test Author"))
                .andExpect(jsonPath("$.genre.id").value(1L))
                .andExpect(jsonPath("$.genre.name").value("Test Genre"));

        verify(bookService, times(1)).findByIdDto(1L);
    }

    @Test
    @DisplayName("должен возвращать статус 404 когда книга не существует")
    void getBook_WhenBookNotExists_ShouldReturnNotFound() throws Exception {
        given(bookService.findByIdDto(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).findByIdDto(999L);
    }

    @Test
    @DisplayName("должен создавать новую книгу")
    void createBook_ShouldCreateBook() throws Exception {
        given(bookService.insert(anyString(), anyLong(), anyLong())).willReturn(book);

        mockMvc.perform(post("/books")
                        .param("title", "New Book")
                        .param("authorId", "1")
                        .param("genreId", "1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author.id").value(1L))
                .andExpect(jsonPath("$.author.fullName").value("Test Author"))
                .andExpect(jsonPath("$.genre.id").value(1L))
                .andExpect(jsonPath("$.genre.name").value("Test Genre"));

        verify(bookService, times(1)).insert("New Book", 1L, 1L);
    }

    @Test
    @DisplayName("должен обновлять существующую книгу")
    void updateBook_ShouldUpdateBook() throws Exception {
        given(bookService.update(anyLong(), anyString(), anyLong(), anyLong())).willReturn(book);

        mockMvc.perform(put("/books/1")
                        .param("title", "Updated Book")
                        .param("authorId", "1")
                        .param("genreId", "1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author.id").value(1L))
                .andExpect(jsonPath("$.author.fullName").value("Test Author"))
                .andExpect(jsonPath("$.genre.id").value(1L))
                .andExpect(jsonPath("$.genre.name").value("Test Genre"));

        verify(bookService, times(1)).update(1L, "Updated Book", 1L, 1L);
    }

    @Test
    @DisplayName("должен удалять книгу")
    void deleteBook_ShouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(1L);
    }
}
