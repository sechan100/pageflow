package org.pageflow.domain.book.controller;


import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BookWriteController {
    
    private final BookService bookService;
    private final AccountService accountService;
    
    
    /**
     * @return react book write form page
     */
    @GetMapping("/book/write")
    public String bookWriteForm() {
        return "forward:/react/build/index.html";
    }
    
    

}
