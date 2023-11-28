package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.model.summary.BookSummary;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Transactional
public class BookWebController {

    private final Rq rq;
    private final BookService bookService;
    private final AccountService accountService;

    
    @GetMapping("/books")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "createdDate") String sortOption) {
        Slice<BookSummary> paging = this.bookService.getList(page, kw, sortOption);
        model.addAttribute("paging",paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sortOption);
        return "/user/book/cards";
    }
    
    
    /**
     * 나의 책 페이지
     */
    @GetMapping("/account/books")
    public String listOfAccount(Model model){
        List<BookSummary> bookSummaries = bookService.getBookSummariesByProfileId(rq.getProfile().getId());
        model.addAttribute("books", bookSummaries);
        return "/user/book/user-books";
    }
    
    
    
    /**
     * @return react book write form page
     */
    @GetMapping("/write/{bookId}")
    public String writeForm(Model model, @PathVariable Long bookId) {
        rq.setRequestAttr("bookId", bookId);
        return "forward:/react/build/book_write/index.html";
    }

}
