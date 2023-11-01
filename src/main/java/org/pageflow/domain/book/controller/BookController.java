package org.pageflow.domain.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.form.BookForm;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;
    private final AccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    @GetMapping("/book")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "createDate") String sortOption) {
        Slice<Book> paging = this.bookService.getList(page, kw, sortOption);
        model.addAttribute("paging",paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sortOption);
        return "/user/book/cards";
    }
    @GetMapping("/api/book/list")
    @ResponseBody
    public List<Book> getBooksApi(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "kw", defaultValue = "") String kw,
                                                  @RequestParam(value = "sortOption", defaultValue = "createDate") String sortOption) {
        Slice<Book> paging = this.bookService.getList(page, kw, sortOption);
        // Log 메시지 출력
        logger.info("Received a request for book list");
        logger.info("Paging Content: {}", paging.getContent());
        logger.info("Paging has content: {}", paging.hasContent());
//        if (!paging.hasContent()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
        logger.info("Keyword: {}", kw);
        logger.info("Sort Option: {}", sortOption);
        return paging.getContent();
    }

    @GetMapping("/book/write")
    public String bookWriteForm() {
      return "/user/book/write";
    }

    @GetMapping("/book/create")
    public String createBook(BookForm bookForm) {
        return "/user/book/book_form";
    }

    @PostMapping("/book/create")
    public String createBook(@Valid BookForm bookForm, BindingResult bindingResult, @RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        if(bindingResult.hasErrors() || file.isEmpty()) {
            return "/user/book/book_form";
        }
        Account author = this.accountService.findByUsernameWithProfile(principal.getName());
        this.bookService.create(bookForm.getTitle(), bookForm.getFile(), author);
        return "redirect:/book";
    }
    
    @GetMapping("/book/vote/{id}")
    public String bookVote(Principal principal, @PathVariable("id") Long id) {
        Book book = this.bookService.getBook(id).orElseThrow();
        Account user = this.accountService.findByUsernameWithProfile(principal.getName());
        this.bookService.vote(book, user);
        return "redirect:/book";
    }
}
