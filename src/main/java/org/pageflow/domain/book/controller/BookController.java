package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;
    private final AccountService accountService;

    @GetMapping("/book")
    public String bookMain(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Book> paging = this.bookService.getList(page, kw);
        model.addAttribute("paging",paging);
        model.addAttribute("kw", kw);
        return "/user/book/cards";
    }
    
    
}
