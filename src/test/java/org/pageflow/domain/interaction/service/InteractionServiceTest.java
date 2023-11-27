package org.pageflow.domain.interaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.domain.interaction.model.Interactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author : sechan
 */
@SpringBootTest
class InteractionServiceTest {
    
    @Autowired
    private InteractionService interactionService;
    
    @Test
    @DisplayName("특정 대상에게 행해진 모든 interaction들을 가져온다.")
    void getAllInteractions() throws JsonProcessingException {
        String targetType = "Book";
        Long targetId = 1L;
        
        Interactions interactions = interactionService.getAllInteractions(targetType, targetId);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(interactions));
    }
}