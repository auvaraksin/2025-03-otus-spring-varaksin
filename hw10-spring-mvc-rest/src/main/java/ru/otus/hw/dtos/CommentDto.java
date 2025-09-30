package ru.otus.hw.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private long id;

    private String text;

    private BookDto book;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.book = new BookDto(comment.getBook());
    }
}
