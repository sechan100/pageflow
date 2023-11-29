package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.entity.Preference;
import org.pageflow.domain.interaction.model.CommentWithPreference;
import org.pageflow.domain.interaction.model.InteractionPair;
import org.pageflow.domain.interaction.model.InteractionsOfTarget;
import org.pageflow.domain.interaction.model.PreferenceStatistics;
import org.pageflow.domain.interaction.service.CommentService;
import org.pageflow.domain.interaction.service.InteractionService;
import org.pageflow.domain.interaction.service.PreferenceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : sechan
 */

@RestController
@RequiredArgsConstructor
@Transactional
public class ApiBookInteractionController {
    
    private final Rq rq;
    private final InteractionService interactionService;
    private final CommentService commentService;
    private final PreferenceService preferenceService;
    private final BookService bookService;
    
    
    // [READ] 책에 관한 모든 interaction 조회
    @GetMapping("/api/books/{bookId}/interactions")
    public InteractionsOfTarget getInteractions(@PathVariable("bookId") Long bookId) {
        InteractionPair<Book> pair = new InteractionPair<>(rq.getProfile(), bookService.repoFindBookById(bookId));
        return interactionService.getAllInteractionsOfTarget(pair);
    }


    // [CREATE] 댓글 생성
    @PostMapping("/api/books/{bookId}/comments")
    public Comment createComment(
            @PathVariable("bookId") Long bookId,
            @RequestParam String content
    ) {
        InteractionPair<Book> pair = new InteractionPair<>(rq.getProfile(), bookService.repoFindBookById(bookId));
        return commentService.createComment(pair, content);
    }
    

    // [UPDATE] 댓글 수정
    @PutMapping("/api/books/{bookId}/comments/{commentId}")
    public Comment updateComment(
            @PathVariable("bookId") Long bookId,
            @PathVariable("commentId") Long commentId,
            @RequestParam String content
    ) {
        return commentService.updateComment(commentId, content);
    }
    
    
    // [DELETE] 댓글 삭제
    @DeleteMapping("/api/books/{bookId}/comments/{commentId}")
    public void deleteComment(
            @PathVariable("bookId") Long bookId,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.deleteComment(commentId);
    }
    
    
    /**
     * [CREATE], [UPDATE] 책에 좋아요 생성 OR 싫어요 생성
     * + 이미 Preference가 존재한다면, 기존의 Preference를 업데이트한다.
     * @param bookId : 책 id
     * @param isLiked : 좋아요인지 싫어요인지 여부
     */
    @PostMapping("/api/books/{bookId}/preferences")
    public Preference createPreferenceOrElseUpdate(
            @PathVariable("bookId") Long bookId,
            @RequestParam boolean isLiked
    ) {
        InteractionPair<Book> pair = new InteractionPair<>(rq.getProfile(), bookService.repoFindBookById(bookId));
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
     * [DELETE] 책과 로그인된 사용자간의 Preference 상호작용을 삭제한다.
     * @param bookId : 책 id
     */
    @DeleteMapping("/api/books/{bookId}/preferences")
    public void deletePreference(
            @PathVariable("bookId") Long bookId
    ) {
        InteractionPair<Book> pair = new InteractionPair<>(rq.getProfile(), bookService.repoFindBookById(bookId));
        preferenceService.deletePreference(pair);
    }
    
    
    /**
     * [READ] 해당 책에 딸린 모든 Preference 상호작용의 통계를 반환한다.
     * @param bookId : 책 id
     * @return 선호 통계
     */
    @GetMapping("/api/books/{bookId}/preferences")
    public PreferenceStatistics getPreference(
            @PathVariable("bookId") Long bookId
    ) {
        InteractionPair<Book> pair = new InteractionPair<>(rq.getProfile(), bookService.repoFindBookById(bookId));
        return preferenceService.getPreferenceStatistics(pair);
    }
}

