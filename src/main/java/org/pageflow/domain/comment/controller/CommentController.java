package org.pageflow.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.comment.form.CommentForm;
import org.pageflow.domain.comment.service.CommentService;
import org.pageflow.domain.hic.test.TestBook;
import org.pageflow.domain.hic.test.TestBookService;
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

    private final TestBookService testBookService;
    private final CommentService commentService;
    private final AccountService accountService;

    @PostMapping("/create/{id}")
    public String createTestBook(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm, BindingResult bindingResult,
                                  Principal principal) {
        TestBook testBook = this.testBookService.getTestBook(id);
        Account author = this.accountService.findByUsernameWithProfile(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("testBook", testBook);
            return "question_detail";
        }
        this.commentService.create(testBook, commentForm.getContent(), author);
        return String.format("redirect:/testBook/detail/%s", id);
    }

}
