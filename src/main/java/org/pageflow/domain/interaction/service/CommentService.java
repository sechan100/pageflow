package org.pageflow.domain.interaction.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.model.CommentWithPreference;
import org.pageflow.domain.interaction.model.InteractionPair;
import org.pageflow.domain.interaction.repository.CommentRepository;
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
    
    
    // [CREATE]
    public Comment createComment(InteractionPair pair, String content) {
        Comment comment = Comment.builder()
                .interactor(pair.getInteractor())
                .content(content)
                .targetType(pair.getTargetType())
                .targetId(pair.getTargetId())
                .build();
        
        Comment persistedComment = commentRepository.save(comment);
        persistedComment.postLoadSetPair();
        return persistedComment;
    }
    
    
    // [UPDATE]
    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findWithInteractorById(commentId);
        comment.setContent(content);
        return comment;
    }
    
    
    /**
     * @param comment 댓글 엔티티
     * @return Comment -> CommentSummary로 가공
     */
    public CommentWithPreference getCommentWithPreference(Comment comment){
        return CommentWithPreference.builder()
                .comment(comment)
                .preferenceStatistics(preferenceService.getPreferenceStatistics(comment.getPair()))
                .build();
        
    }
    
    
    public List<CommentWithPreference> getCommentsWithPreference(InteractionPair pair) {
        List<Comment> comments = commentRepository.findAllByTargetTypeAndTargetId(pair.getTargetType(), pair.getTargetId());
        return comments.stream()
                .map(this::getCommentWithPreference)
                .toList();
    }
    
    
    // [DELETE]
    public void deleteComment(Long commentId) {
        Comment commentToDelete = commentRepository.findWithInteractorById(commentId);
        // 댓글을 삭제한다.
        commentRepository.delete(commentToDelete);
        // 댓글을 참조하는 모든 preference들을 삭제한다.
        preferenceService.deleteAllPreferences(commentToDelete.getPair());
    }
    
    
    // [DELETE] all: interactor와는 관계 없고, target에 딸린 모든 댓글을 삭제한다.
    public void deleteAllCommentByTarget(InteractionPair pair) {
        
        // 타겟에 딸린 모든 댓글을 가져옴
        List<Comment> commentsToDelete = commentRepository.findAllByTargetTypeAndTargetId(pair.getTargetType(), pair.getTargetId());
        
        // 타겟에 참조된 모든 댓글을 삭제.
        commentRepository.deleteAllByTargetTypeAndTargetId(pair.getTargetType(), pair.getTargetId());
        
        // 댓글을 참조하는 모든 preference들을 삭제.
        commentsToDelete.forEach(comment -> preferenceService.deleteAllPreferences(comment.getPair()));
    }
    
    
}
