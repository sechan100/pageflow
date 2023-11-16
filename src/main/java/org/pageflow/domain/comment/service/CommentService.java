package org.pageflow.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.comment.entity.Comment;
import org.pageflow.domain.comment.repository.CommentRepository;
import org.pageflow.domain.user.entity.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void create(Book book, String content, Profile author) {
        Comment comment = Comment
                .builder()
                .content(content)
                .book(book)
                .author(author)
                .build();
        this.commentRepository.save(comment);
    }// 댓글 작성

    public Comment getComment(Long id) {
        return this.commentRepository.findById(id).orElseThrow();
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    } // 댓글 수정

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    } //댓글 삭제

    public List<Comment> findAll() {
        return this.commentRepository.findAll();
    }

    public List<Comment> findAllByOrderByCreateDateDesc(Long id) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        return commentRepository.findAllByBookId(id, sort);
    }
}
