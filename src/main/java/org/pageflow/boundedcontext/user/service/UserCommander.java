package org.pageflow.boundedcontext.user.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.UserFetchDepth;
import org.pageflow.boundedcontext.user.entity.Account;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.boundedcontext.user.model.user.AggregateUser;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.pageflow.boundedcontext.user.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class UserCommander {
    
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    
    /**
     * 서로 연관이 없는 새로운 Account와 Profile의 인스턴스를 적절한 순서로 연관관계를 지은 후 저장한다. <br>
     * 누가 먼저 영속화되냐, account에 profile이 있냐 profile에 account가 있냐에 따라서 에러가 발생할 수 있기 때문
     */
    @Transactional
    public AggregateUser saveUser(Account account, Profile profile) {
        
        // Account 먼저 영속
        Account savedAccount = accountRepository.save(account);
        
        // 영속된 account를 영속되지 않은 profile과 연관지음
        profile.associateAccount(savedAccount);
        
        // Profile 영속
        Profile savedProfile = profileRepository.save(profile);
        
        return AggregateUser.builder()
                .fetchDepth(UserFetchDepth.FULL)
                .account(savedAccount)
                .profile(savedProfile)
                .build();
    }
}
