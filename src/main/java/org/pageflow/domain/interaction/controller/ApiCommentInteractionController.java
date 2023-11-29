package org.pageflow.domain.interaction.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.entity.Preference;
import org.pageflow.domain.interaction.model.InteractionPair;
import org.pageflow.domain.interaction.repository.CommentRepository;
import org.pageflow.domain.interaction.service.CommentService;
import org.pageflow.domain.interaction.service.PreferenceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
@Transactional
public class ApiCommentInteractionController {
    
    private final Rq rq;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final PreferenceService preferenceService;
    
    
    /**
     * [CREATE], [UPDATE] 댓글에에 좋아요 생성 OR 싫어요 생성
     * + 이미 Preference가 존재한다면, 기존의 Preference를 업데이트한다.
     * @param commentId : 책 id
     * @param isLiked : 좋아요인지 싫어요인지 여부
     */
    @PostMapping("/api/comments/{commentId}/preferences")
    public Preference createPreferenceOrElseUpdate(
            @PathVariable("commentId") Long commentId,
            @RequestParam boolean isLiked
    ) {
        InteractionPair<Comment> pair = new InteractionPair<>(rq.getProfile(), commentRepository.findWithInteractorById(commentId));
        Preference preference = preferenceService.findPreferenceOrElseNull(pair);
        
        // 기존 Preference가 존재한다면, 기존 Preference를 업데이트한다.
        if (preference != null) {
            return preferenceService.updatePreferenceIsLiked(pair, isLiked);
            
            // 기존 Preference가 존재하지 않는다면, 새로 Preference를 생성한다.
        } else {
            return preferenceService.createPreference(pair, isLiked);
        }
    }
    
    
    /**
     * [DELETE] 댓글과 현재 로그인된 사용자간의 Preference 상호작용을 삭제한다.
     * @param commentId : 댓글 id
     */
    @DeleteMapping("/api/comments/{commentId}/preferences")
    public void deletePreference(
            @PathVariable("commentId") Long commentId
    ) {
        InteractionPair<Comment> pair = new InteractionPair<>(rq.getProfile(), commentRepository.findWithInteractorById(commentId));
        preferenceService.deletePreference(pair);
    }
}
