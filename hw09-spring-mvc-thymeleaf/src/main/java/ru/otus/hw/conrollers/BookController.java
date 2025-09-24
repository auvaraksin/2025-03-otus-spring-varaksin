package ru.otus.hw.conrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping("/")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        List<Comment> comments = commentService.findAllByBookId(id);
        model.addAttribute("comments", comments);

        model.addAttribute("book", book);
        return "books/view";
    }

    @GetMapping("/books/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/form";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable long id, Model model) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/form";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Book book,
                           @RequestParam long authorId,
                           @RequestParam long genreId) {
        if (book.getId() == 0) {
            bookService.insert(book.getTitle(), authorId, genreId);
        } else {
            bookService.update(book.getId(), book.getTitle(), authorId, genreId);
        }
        return "redirect:/";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBookForm(@PathVariable long id, Model model) {
        model.addAttribute("book", bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found")));
        return "books/delete";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }
}
