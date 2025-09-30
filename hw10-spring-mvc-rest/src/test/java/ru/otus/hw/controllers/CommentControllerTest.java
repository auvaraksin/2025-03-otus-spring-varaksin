package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.conrollers.CommentController;
import ru.otus.hw.dtos.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@DisplayName("REST контроллер для работы с комментариями")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private final Author author = new Author(1L, "Test Author");
    private final Genre genre = new Genre(1L, "Test Genre");
    private final Book book = new Book(1L, "Test Book", author, genre);
    private final Comment comment = new Comment(1L, "Test Comment", book);

    @Test
    @DisplayName("должен возвращать список комментариев по ID книги")
    void getCommentsByBook_ShouldReturnCommentsList() throws Exception {
        given(commentService.findAllByBookIdDto(1L)).willReturn(List.of(
                new CommentDto(comment)
        ));

        mockMvc.perform(get("/book/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].text").value("Test Comment"))
                .andExpect(jsonPath("$[0].book.id").value(1L))
                .andExpect(jsonPath("$[0].book.title").value("Test Book"))
                .andExpect(jsonPath("$[0].book.author.id").value(1L))
                .andExpect(jsonPath("$[0].book.author.fullName").value("Test Author"))
                .andExpect(jsonPath("$[0].book.genre.id").value(1L))
                .andExpect(jsonPath("$[0].book.genre.name").value("Test Genre"));

        verify(commentService, times(1)).findAllByBookIdDto(1L);
    }

    @Test
    @DisplayName("должен создавать новый комментарий")
    void createComment_ShouldCreateComment() throws Exception {
        given(commentService.save(anyLong(), anyString())).willReturn(comment);

        mockMvc.perform(post("/comments")
                        .param("bookId", "1")
                        .param("text", "New Comment")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test Comment"))
                .andExpect(jsonPath("$.book.id").value(1L))
                .andExpect(jsonPath("$.book.title").value("Test Book"))
                .andExpect(jsonPath("$.book.author.id").value(1L))
                .andExpect(jsonPath("$.book.author.fullName").value("Test Author"))
                .andExpect(jsonPath("$.book.genre.id").value(1L))
                .andExpect(jsonPath("$.book.genre.name").value("Test Genre"));

        verify(commentService, times(1)).save(1L, "New Comment");
    }

    @Test
    @DisplayName("должен удалять комментарий")
    void deleteComment_ShouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("должен возвращать пустой список когда у книги нет комментариев")
    void getCommentsByBook_WhenNoComments_ShouldReturnEmptyList() throws Exception {
        given(commentService.findAllByBookIdDto(2L)).willReturn(List.of());

        mockMvc.perform(get("/book/2/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(commentService, times(1)).findAllByBookIdDto(2L);
    }
}
