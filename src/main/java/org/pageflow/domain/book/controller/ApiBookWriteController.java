package org.pageflow.domain.book.controller;


import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.model.outline.Outline;
import org.pageflow.domain.book.model.request.PageUpdateRequest;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.book.service.BookWriteService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiBookWriteController {
    
    private final Rq rq;
    private final BookService bookService;
    private final BookWriteService bookWriteService;
    private final AccountService accountService;
    
    
    
    
    @GetMapping("/api/book/new")
    public Book createNewBook() {
        // 비지니스 요구에 따라, 새 책을 생성하지 못하는 경우를 처리하는 로직 추가 예정(ex. 현재 작성중인 책이 너무 많은경우)
        return bookWriteService.createNewBook(rq.getAccount().getProfile());
    }
    
    @GetMapping("/api/book/page/update")
    public void updatePage(
            @RequestParam(name = "id") Long pageId,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "orderNum") Integer orderNum
    ) {
        bookWriteService.updatePage(new PageUpdateRequest(
                pageId,
                title,
                orderNum,
                content
        ));
    }
    
    
    @GetMapping("/api/book/outline")
    public Outline getBookOutlineData(
            @RequestParam(name = "id") Long bookId
    ) {
        
        return bookService.getOutline(bookId);

    }
    
    
}
