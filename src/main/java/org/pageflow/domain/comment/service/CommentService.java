package org.pageflow.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.DataNotFoundException;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.comment.entity.Comment;
import org.pageflow.domain.comment.repository.CommentRepository;
import org.pageflow.domain.user.entity.Account;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }
}
