package org.pageflow.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.comment.entity.Comment;
import org.pageflow.domain.comment.form.CommentForm;
import org.pageflow.domain.comment.service.CommentService;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final AccountService accountService;
    private final BookService bookService;

//    @PostMapping("/create/{id}")
//    public String create(Model model, @PathVariable("id") Long id, @Valid CommentForm commentForm,
//                         BindingResult bindingResult, Principal principal) {
//        Book book = this.bookService.delegateFindBookWithAuthorById(id);
//        Account author = this.accountService.findFetchJoinProfileByUsername(principal.getName());
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("book", book);
//            return "/book_detail";
//        }
//        this.commentService.create(book, commentForm.getContent(),author);
//        return String.format("redirect:/book/detail/%s", id);
//    } //댓글 작성

    @GetMapping("/create/{id}")
    public ResponseEntity<List<Comment>> create(@RequestParam(value="content") String content, @PathVariable("id") Long id, Principal principal){
        Book book = this.bookService.delegateFindBookWithAuthorById(id);
        Account author = this.accountService.findFetchJoinProfileByUsername(principal.getName());

        this.commentService.create(book, content,author);
        List<Comment> comment = this.commentService.findAll();

        return ResponseEntity.ok().body(comment);

    } // 댓글 작성

    @GetMapping("/list/{id}")
    public ResponseEntity<List<Comment>> list(){
        List<Comment> comment = this.commentService.findAll();

        return ResponseEntity.ok().body(comment);

    } // 댓글 작성

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(Model model,CommentForm commentForm, @PathVariable("id") Long id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        Book book = this.bookService.delegateFindBookWithAuthorById(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        model.addAttribute("commentForm", commentForm);
        return String.format("redirect:/book/detail/%s",book.getId());
    } // 수정 폼

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid CommentForm commentForm, BindingResult bindingResult,
                               @PathVariable("id") Long id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return String.format("redirect:/book/detail/%s", id);
        }
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/book/detail/%s", comment.getBook().getId());
    } // 수정

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(Principal principal, @PathVariable("id") Long id) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/book/detail/%s", comment.getBook().getId());
    } // 삭제

    @GetMapping("/vote/{id}")
    public String vote(Principal principal, @PathVariable("id") Long id) {
        Comment comment = this.commentService.getComment(id);
        Account user = this.accountService.findFetchJoinProfileByUsername(principal.getName());
        if(!comment.getVoter().contains(user)){
            this.commentService.vote(comment, user);
        } else {
            this.commentService.deletelVote(comment, user);
        }
        return String.format("redirect:/book/detail/%s", id);
    } //추천 및 추천취소

}
