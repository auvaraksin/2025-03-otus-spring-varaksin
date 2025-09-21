package ru.otus.hw.conrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final BookService bookService;

    @PostMapping("/comments/save")
    public String saveComment(@RequestParam long bookId,
                              @RequestParam String text) {
        commentService.save(bookId, text);
        return "redirect:/books/" + bookId;
    }

    @GetMapping("/comments/delete/{id}")
    public String deleteCommentForm(@PathVariable long id, Model model) {
        var comment = commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        model.addAttribute("comment", comment);
        return "comments/delete";
    }

    @PostMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable long id) {
        var comment = commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentService.deleteById(id);
        return "redirect:/books/" + comment.getBook().getId();
    }
}
