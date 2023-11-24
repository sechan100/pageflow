package org.pageflow.domain.comment.repository;

import org.pageflow.domain.comment.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBookId(Long id, Sort sort);

    Long findAllByBookId(Long id);
    List<Comment> findAllByOrderByCreateDateDesc();

    Long countByBookId(Long id);
}