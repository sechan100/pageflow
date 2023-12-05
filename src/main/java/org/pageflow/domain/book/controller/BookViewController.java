package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.constants.BookStatus;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
@Transactional
public class BookViewController {

    private final BookService bookService;
    private final Rq rq;
    
    
    
    /**
     * @return react book read form page
     */
    @GetMapping("/viewer/{bookId}")
    public String bookViewForm(@PathVariable("bookId") Long bookId) {
        
        Book book = bookService.repoFindBookById(bookId);
        
        String viewerUrl = "forward:/react/build/book_viewer/index.html";
        
        // 출판된 책인 경우 허용
        if(book.getStatus().equals(BookStatus.PUBLISHED)){
            return viewerUrl;
            
        // 출판은 안되었지만, 작가나 관리자는 허용
        } else if(rq.getUserSession().isAdmin() || rq.getUserSession().getId().equals(book.getAuthor().getId())) {
            return viewerUrl;
        }
        
        throw new IllegalArgumentException("책이 출판되지 않았습니다.");
    }

}
