package org.pageflow.domain.interaction.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.model.CommentSummary;
import org.pageflow.domain.interaction.repository.CommentRepository;
import org.pageflow.domain.user.model.dto.UserSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    
    private final PreferenceService preferenceService;
    private final CommentRepository commentRepository;
    
    
    
    
    
    
    /**
     * @param comment 댓글 엔티티
     * @return Comment -> CommentSummary로 가공
     */
    public CommentSummary getCommentSummary(Comment comment){
        return CommentSummary.builder()
                .targetId(comment.getTargetId())
                .targetType(comment.getTargetType())
                .id(comment.getId())
                .content(comment.getContent())
                .author(new UserSession(comment.getInteractor()))
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .preferenceStatistics(preferenceService.getPreferenceStatistics(comment.getClass().getSimpleName(), comment.getId()))
                .build();
        
    }
    
    public List<CommentSummary> getCommentSummaries(String targetType, Long targetId) {
        List<Comment> comments = commentRepository.findAllByTargetTypeAndTargetId(targetType, targetId);
        return comments.stream()
                .map(this::getCommentSummary)
                .toList();
    }
}
