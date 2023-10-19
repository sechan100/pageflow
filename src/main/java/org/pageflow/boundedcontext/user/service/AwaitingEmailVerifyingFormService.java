package org.pageflow.boundedcontext.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.model.dto.AwaitingEmailVerifyingRedisEntity;
import org.pageflow.boundedcontext.user.repository.AwaitingEmailVerifyingFormRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwaitingEmailVerifyingFormService {
    
    private final AwaitingEmailVerifyingFormRepository emailCacheRepository;
    
    public void save(AwaitingEmailVerifyingRedisEntity form) {
        emailCacheRepository.save(form);
    }
    
    public boolean existsById(String email) {
        return emailCacheRepository.existsById(email);
    }
    
    public AwaitingEmailVerifyingRedisEntity findById(String email) {
        return emailCacheRepository.findById(email).orElseThrow();
    }
    
    public void delete(String email) {
        emailCacheRepository.deleteById(email);
    }
}
