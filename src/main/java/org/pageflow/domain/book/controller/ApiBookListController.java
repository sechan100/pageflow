package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.model.summary.BookSummary;
import org.pageflow.domain.book.service.BookService;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiBookListController {

    private final BookService bookService;
    
    @GetMapping("/api/books/list")
    public List<BookSummary> getBooksApi(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "kw", defaultValue = "") String kw,
                                  @RequestParam(value = "sortOption", defaultValue = "createdDate") String sortOption) {
        Slice<BookSummary> paging = this.bookService.getList(page, kw, sortOption);
        return paging.getContent();
    }
}
