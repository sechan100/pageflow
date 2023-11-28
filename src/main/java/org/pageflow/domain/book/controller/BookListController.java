package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookListController {

    private final BookService bookService;

    @GetMapping("/books")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "createDate") String sortOption) {
        Slice<Book> paging = this.bookService.getList(page, kw, sortOption);
        model.addAttribute("paging",paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sortOption);
        return "/user/book/cards";
    }

    @GetMapping("/api/books/list")
    @ResponseBody
    public List<Book> getBooksApi(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "kw", defaultValue = "") String kw,
                                  @RequestParam(value = "sortOption", defaultValue = "createDate") String sortOption) {
        Slice<Book> paging = this.bookService.getList(page, kw, sortOption);
        return paging.getContent();
    }
}
