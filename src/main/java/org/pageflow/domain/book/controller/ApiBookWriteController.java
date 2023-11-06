package org.pageflow.domain.book.controller;


import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiBookWriteController {
    
    private final Rq rq;
    private final BookService bookService;
    private final AccountService accountService;
    
    
    
    
    
    @GetMapping("/api/book/new")
    public Book createNewBook() {
        // 조건에 따라 새 책을 생성하지 못하는 경우를 처리(ex. 현재 작성중인 책이 너무 많은경우)
        return bookService.createNewBook(rq.getAccount());
    }

    
    
}
