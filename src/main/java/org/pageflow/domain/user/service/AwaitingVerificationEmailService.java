package org.pageflow.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.pageflow.domain.user.entity.AwaitingEmailVerificationRequest;
import org.pageflow.domain.user.repository.AwaitingVerificationEmailRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwaitingVerificationEmailService {
    
    private final AwaitingVerificationEmailRepository emailCacheRepository;
    
    public void save(AwaitingEmailVerificationRequest awaitingVerificationEmail) {
        emailCacheRepository.save(awaitingVerificationEmail);
    }
    
    public boolean existsById(String email) {
        return emailCacheRepository.existsById(email);
    }
    
    public AwaitingEmailVerificationRequest findById(String email) {
        return emailCacheRepository.findById(email).orElseThrow();
    }
    
    public void delete(String email) {
        emailCacheRepository.deleteById(email);
    }
}
