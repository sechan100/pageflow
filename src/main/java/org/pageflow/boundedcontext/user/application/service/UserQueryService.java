package org.pageflow.boundedcontext.user.application.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.application.acl.LoadAccountAcl;
import org.pageflow.boundedcontext.auth.application.acl.LoadSessionUserAcl;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.EncryptedPassword;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.AccountJpaEntity;
import org.pageflow.boundedcontext.user.adapter.out.persistence.repository.AccountJpaRepository;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.global.api.code.Code3;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : sechan
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService implements LoadAccountAcl, LoadSessionUserAcl {
    private final AccountJpaRepository accountJpaRepository;


    @Override
    public Optional<Account> loadAccount(String username) {
        Optional<AccountJpaEntity> op = accountJpaRepository.findByUsername(username);
        return op.map(this::toAccount);
    }

    @Override
    public Optional<Account> loadAccount(UID uid) {
        Optional<AccountJpaEntity> op = accountJpaRepository.findById(uid.toLong());
        return op.map(this::toAccount);
    }

    private Account toAccount(AccountJpaEntity entity) {
        return new Account(
            UID.from(entity.getId()),
            entity.getUsername(),
            EncryptedPassword.of(entity.getPassword()),
            entity.getEmail(),
            entity.getRole()
        );
    }

    @Override
    public UserDto.Session loadSessionUser(UID uid) {
        return accountJpaRepository.findWithProfileById(uid.toLong())
            .map(entity -> new UserDto.Session(
                TSID.from(entity.getId()),
                entity.getUsername(),
                entity.getEmail(),
                entity.isEmailVerified(),
                entity.getRole(),
                entity.getProfile().getPenname(),
                entity.getProfile().getProfileImageUrl()
                )
            ).orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("사용자를 찾을 수 없습니다"));
    }
}
