package org.pageflow.domain.support.controller;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.support.model.dto.QuestionForm;
import org.pageflow.domain.support.model.entity.Question;
import org.pageflow.domain.support.service.SupportService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RequiredArgsConstructor
@Transactional
@RestController
public class ApiSupportController {
    
    private final SupportService supportService;
    
    @PostMapping("/api/question")
    public Question createQuestion(@RequestBody QuestionForm form){
        return supportService.createQuestion(form);
    }
}
