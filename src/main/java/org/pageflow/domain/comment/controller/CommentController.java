package org.pageflow.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.comment.form.CommentForm;
import org.pageflow.domain.comment.service.CommentService;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final AccountService accountService;
    private final BookService bookService;

    @PostMapping("/create/{id}")
    public String create(Model model, @PathVariable("id") Long id, @Valid CommentForm commentForm, BindingResult bindingResult,
                                  Principal principal) {
        Book book = this.bookService.getBook(id);
        Account author = this.accountService.findByUsernameWithProfile(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", book);
            return "/book_detail";
        }
        this.commentService.create(book, commentForm.getContent(), author);
        return String.format("redirect:/book/detail/%s", id);
    }

}
