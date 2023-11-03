package org.pageflow.domain.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.form.BookForm;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    public String bookDetail(Model model, @PathVariable("id") Long id){
        Book book = this.bookService.getBook(id);
        model.addAttribute("book", book);
        return "/book/book_detail";
    } // 상세페이지

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/book/modify/{id}")
    public String bookModify(BookForm bookForm, @PathVariable("id") Long id, Principal principal) {
        Book book = this.bookService.getBook(id);
        if(!book.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        bookForm.setTitle(book.getTitle());
        bookForm.setFile(bookForm.getFile());
        return "/book/book_form";
    } // 수정 get

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/book/modify/{id}")
    public String bookModify(@Valid BookForm bookForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws IOException {
        if (bindingResult.hasErrors() || file.isEmpty()) {
            return "/book/book_form";
        }

        Book book = this.bookService.getBook(id);
        Account author = this.accountService.findByUsernameWithProfile(principal.getName());
        if (!book.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        Book modifiedBook = this.bookService.modify(book,bookForm.getTitle(), file, author);
        return "redirect:/book/list";

    } // 수정 post



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/book/delete/{id}")
    public String bookDelete(Principal principal, @PathVariable("id") Long id) {
        Book book = this.bookService.getBook(id);
        if (!book.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.bookService.delete(book);
        return "redirect:/book/list";
    } // 삭제

    @GetMapping("/book/vote/{id}")
    public String deletelVote(Principal principal, @PathVariable("id") Long id) {
        Book book = this.bookService.getBook(id);
        Account user = this.accountService.findByUsernameWithProfile(principal.getName());
        if(!book.getVoter().contains(user)){
            this.bookService.vote(book, user);
        } else {
            this.bookService.deletelVote(book, user);
        }
      return String.format("redirect:/book/detail/%s", id);
    } //추천 및 추천취소

}
