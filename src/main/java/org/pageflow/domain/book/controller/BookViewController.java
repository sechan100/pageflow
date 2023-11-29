package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@Controller
@RequiredArgsConstructor
@Transactional
public class BookViewController {

    private  final BookService bookService;

    /**
     * @return react book read form page
     */
    @GetMapping("/viewer/{bookId}")
    public String bookViewForm(@PathVariable("bookId") Long bookId) {
        return "forward:/react/build/book_viewer/index.html";
    }

}
