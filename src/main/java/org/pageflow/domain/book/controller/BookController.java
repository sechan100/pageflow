package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.book.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;

    @GetMapping("/book/list")
    public String list(Model model) {
        List<Book> books = this.bookService.getList();
        model.addAttribute("books",books);
        return "/book/book_list";
    }
}
