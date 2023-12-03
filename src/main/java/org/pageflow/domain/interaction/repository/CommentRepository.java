package org.pageflow.domain.interaction.repository;

import org.pageflow.domain.interaction.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : sechan
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @EntityGraph(attributePaths = {"interactor"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Comment> findAllWithInteractorByTargetTypeAndTargetId(String targetType, Long targetId);
    
    @EntityGraph(attributePaths = {"interactor"}, type = EntityGraph.EntityGraphType.LOAD)
    Comment findWithInteractorById(long commentId);
    
    void deleteAllByTargetTypeAndTargetId(String targetType, Long targetId);
}
