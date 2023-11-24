package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.model.form.BookForm;
import org.pageflow.domain.book.model.outline.Outline;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.comment.entity.Comment;
import org.pageflow.domain.comment.service.CommentService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;
    private final AccountService accountService;
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/book/detail/{id}")
    public String bookDetail(Model model, @PathVariable("id") Long id) {
        Outline outline = this.bookService.getOutline(id);
        List<Comment> comment = this.commentService.findAllByOrderByCreateDateDesc(id);
        model.addAttribute("comment", comment);
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
