package org.pageflow.domain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.comment.entity.Comment;
import org.pageflow.domain.comment.service.CommentService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final AccountService accountService;
    private final BookService bookService;
    private final Rq rq;

    @PostMapping("/create/{id}")
    public ResponseEntity<List<Comment>> create(@RequestParam(value = "content") String content, @PathVariable("id") Long id, Principal principal) {
        Book book = this.bookService.repoFindBookWithAuthorById(id);

        this.commentService.create(book, content, rq.getAccount().getProfile());
        List<Comment> commentList = this.commentService.findAll();

        return ResponseEntity.ok().body(commentList);
    } // 댓글 작성

    @GetMapping("/list/{id}")
    public ResponseEntity<List<Comment>> list(@PathVariable("id") Long id) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        List<Comment> commentList = this.commentService.findAllByOrderByCreateDateDesc(id);

        return ResponseEntity.ok().body(commentList);
    } // 댓글 리스트 조회

    public Map<String, Object> getCommentListAndCount(@PathVariable("id") Long id) {
        Map<String, Object> result = new HashMap<>();

        List<Comment> commentList = this.commentService.findAllByOrderByCreateDateDesc(id);

        Long commentCount = this.commentService.countByBookId(id);

        result.put("commentList", commentList);
        result.put("commentCount", commentCount);

        return result;
    }
    // 댓글 개수 가져오기


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public ResponseEntity<List<Comment>> modify(@RequestParam(value = "content") String content, @PathVariable("id") Long id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getAccount().getUsername().equals(rq.getUserSession().getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.commentService.modify(comment, content);
        List<Comment> commentList = this.commentService.findAll();

        return ResponseEntity.ok().body(commentList);
    }// 수정

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public ResponseEntity<List<Comment>> commentDelete(Principal principal, @PathVariable("id") Long id) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getAccount().getUsername().equals(rq.getUserSession().getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.commentService.delete(comment);
        List<Comment> commentList = this.commentService.findAllByOrderByCreateDateDesc(comment.getBook().getId());
        return ResponseEntity.ok().body(commentList);
    } // 삭제

}
