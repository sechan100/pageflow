package org.pageflow.domain.interaction.repository;

import org.pageflow.domain.interaction.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : sechan
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findAllByTargetTypeAndTargetId(String targetType, Long targetId);
}
