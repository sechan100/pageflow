package org.pageflow.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.DataNotFoundException;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.comment.entity.Comment;
import org.pageflow.domain.comment.repository.CommentRepository;
import org.pageflow.domain.user.entity.Account;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void create(Book book, String content, Account author) {
        Comment comment = Comment
                .builder()
                .content(content)
                .book(book)
                .author(author)
                .build();
        this.commentRepository.save(comment);
    }// 댓글 작성

    public Comment getComment(Long id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        } // 댓글 조회
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    } // 댓글 수정

    public void delete(Comment comment){
        this.commentRepository.delete(comment);
    } //댓글 삭제

    public void vote(Comment comment, Account user) {
        comment.getVoter().add(user);
        this.commentRepository.save(comment);
    }
    // 추천
    public void deletelVote(Comment comment, Account user) {
        comment.getVoter().remove(user);
        this.commentRepository.save(comment);
    }


    public List<Comment> findAll() {
        return this.commentRepository.findAll();
    }

    public List<Comment> findAllByBookId(Long id) {
        return this.commentRepository.findAllByBookId(id);
    }
}
