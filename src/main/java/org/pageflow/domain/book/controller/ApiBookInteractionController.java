package org.pageflow.domain.book.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.interaction.entity.Comment;
import org.pageflow.domain.interaction.service.CommentService;
import org.pageflow.domain.interaction.service.PreferenceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */

@RestController
@RequiredArgsConstructor
@Transactional
public class ApiBookInteractionController {
    
    private final Rq rq;
    private final CommentService commentService;
    private final PreferenceService preferenceService;
    
    @PostMapping("/api/books/{bookId}/comments")
    public Comment createComment(
            @PathVariable("bookId") Long bookId,
            @RequestParam String content
    ) {
//        InteractionPair pair = new InteractionPair<>(rq.getAccount().getProfile(), bookId);
        
//        commentService.createComment(pair, content);
        return null;
    }

}

