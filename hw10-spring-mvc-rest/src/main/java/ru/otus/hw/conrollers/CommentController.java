package ru.otus.hw.conrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dtos.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comments/book/{bookId}")
    public ResponseEntity<List<CommentDto>> getCommentsByBook(@PathVariable long bookId) {
        return ResponseEntity.ok(commentService.findAllByBookIdDto(bookId));
    }

    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@RequestParam long bookId,
                                                 @RequestParam String text) {
        Comment comment = commentService.save(bookId, text);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable long id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
