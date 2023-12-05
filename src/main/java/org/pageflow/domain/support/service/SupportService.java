package org.pageflow.domain.support.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.support.model.dto.QuestionForm;
import org.pageflow.domain.support.model.entity.Question;
import org.pageflow.domain.support.repository.AnswerRepository;
import org.pageflow.domain.support.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SupportService {
    
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final Rq rq;
    
    public Question createQuestion(QuestionForm form) {
        Question question = Question.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .questioner(rq.getProfile())
                .build();
        
        return questionRepository.save(question);
    }
}
