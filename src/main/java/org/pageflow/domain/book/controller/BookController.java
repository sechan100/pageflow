package org.pageflow.domain.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.form.BookForm;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;
    private final AccountService accountService;

    @GetMapping("/book/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Book> paging = this.bookService.getList(page, kw);
        model.addAttribute("paging",paging);
        model.addAttribute("kw", kw);
        return "/book/book_list";
    }
    
    @GetMapping("/book/create")
    public String createBook(BookForm bookForm) {
      return "/book/book_form";
    }
    
    @PostMapping("/book/create")
    public String createBook(@Valid BookForm bookForm, BindingResult bindingResult, @RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        if(bindingResult.hasErrors() || file.isEmpty()) {
            return "/book/book_form";
        }
        Account author = this.accountService.findByUsernameWithProfile(principal.getName());
        this.bookService.create(bookForm.getTitle(), bookForm.getFile(), author);
        return "redirect:/book/list";
    }

    @GetMapping(value = "/book/detail/{id}")
    public String bookDetail(Model model, @PathVariable("id") Integer id){
        Book book = this.bookService.getBook(id);
        model.addAttribute("book", book);
        return "/book/book_detail";
    }
    
    @GetMapping("/book/vote/{id}")
    public String bookVote(Principal principal, @PathVariable("id") Long id) {
        Book book = this.bookService.getBook(id).orElseThrow();
        Account user = this.accountService.findByUsernameWithProfile(principal.getName());
        this.bookService.vote(book, user);
        return "redirect:/book/list";
    }
}
