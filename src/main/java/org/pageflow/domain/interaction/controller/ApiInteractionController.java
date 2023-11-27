package org.pageflow.domain.interaction.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.interaction.service.InteractionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
@Transactional
public class ApiInteractionController {
    
    private final InteractionService interactionService;
    
    /**
     * 특정 대상에게 행해진 모든 interaction들을 가져온다.
     * @param targetType 상호작용의 타겟이 되는 대상의 타입
     * @param targetId  상호작용의 타겟이 되는 대상의 아이디
     */
    @GetMapping("/api/interactions")
    public void getAllInteractions(
            @RequestParam("type") String targetType,
            @RequestParam("id") Long targetId
    ) {
        interactionService.getAllInteractions(targetType, targetId);
    }
}





