//package org.pageflow.domain.comment.service;
//
//import lombok.RequiredArgsConstructor;
//import org.pageflow.domain.comment.entity.Comment;
//import org.pageflow.domain.comment.repository.CommentRepository;
//import org.pageflow.domain.hic.test.TestBook;
//import org.pageflow.domain.user.entity.Account;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CommentService {
//
//    private final CommentRepository commentRepository;
//
//    public void create(TestBook testBook, String content, Account author) {
//        Comment comment = Comment
//                .builder()
//                .content(content)
//                .testBook(testBook)
//                .author(author)
//                .build();
//        this.commentRepository.save(comment);
//    }
//}
