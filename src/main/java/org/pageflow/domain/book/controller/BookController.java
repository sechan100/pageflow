package org.pageflow.domain.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.model.form.BookForm;
import org.pageflow.domain.book.model.outline.Outline;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;
    private final AccountService accountService;

    @GetMapping("/book")
    public String bookMain(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Book> paging = this.bookService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "/user/book/cards";
    }

    @GetMapping(value = "/book/detail/{id}")
    public String bookDetail(Model model, @PathVariable("id") Long id) {
        Outline outline = this.bookService.getOutline(id);
        model.addAttribute("outline", outline);
        model.addAttribute("chapters", outline.getChapters());
        return "/user/book/book_detail";
    } // 상세페이지

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/book/modify/{id}")
    public String bookModify(BookForm bookForm, @PathVariable("id") Long id, Principal principal) {
        Book book = this.bookService.repoFindBookWithAuthorById(id);
        if (!book.getAuthor().getNickname().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        bookForm.setTitle(book.getTitle());
        bookForm.setFile(bookForm.getFile());
        return "forward:/react/build/index.html\";";
    } // 수정 get

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/book/modify/{id}")
    public String bookModify(@Valid BookForm bookForm, BindingResult bindingResult,
                             Principal principal, @PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws IOException {
        if (bindingResult.hasErrors() || file.isEmpty()) {
            return "forward:/react/build/index.html\";";
        }

        Book book = this.bookService.repoFindBookWithAuthorById(id);
        Account author = this.accountService.findFetchJoinProfileByUsername(principal.getName());
        if (!book.getAuthor().getNickname().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        Book modifiedBook = this.bookService.modify(book, bookForm.getTitle(), file, author);
        return "redirect:/user/book/cards";

    } // 수정 post

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/book/delete/{id}")
    public String bookDelete(Principal principal, @PathVariable("id") Long id) {
        Book book = this.bookService.repoFindBookWithAuthorById(id);
        if (!book.getAuthor().getNickname().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.bookService.delete(book);
        return "redirect:/user/book/cards";
    } // 삭제

}
