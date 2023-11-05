package org.pageflow.domain.book.controller;


import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.constants.BookFetchType;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    
    
    
    
    
    
    /**
     * @param bookId    책 아이디
     * @param fetchScope    가져올 데이터의 범위를 설정한다. book, chapter, page 중 하나를 받고, 기본값은 book이다.
     *                      예를 들어 chapter를 설정하면, book과 chapter 데이터를 가져온다. chapter의 pages 배열은 null이 된다.
     * @return 책 데이터
     */
    @GetMapping("/api/book")
    public Book getBookData(
            @RequestParam(name = "id") Long bookId,
            @RequestParam(name = "scope", defaultValue = "book") String fetchScope
    ) {
        
        return switch(fetchScope) {
            case "chapter" -> bookService.findWithScopeById(bookId, BookFetchType.CHAPTER);
            case "page" -> bookService.findWithScopeById(bookId, BookFetchType.PAGE);
            default -> bookService.findWithScopeById(bookId, BookFetchType.BOOK);
        };

    }
    
    
}
