package org.pageflow.boundedcontext.user.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.application.acl.LoadSessionUserAcl;
import org.pageflow.boundedcontext.auth.domain.Account;
import org.pageflow.boundedcontext.auth.domain.EncryptedPassword;
import org.pageflow.boundedcontext.auth.port.out.AccountPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.AccountJpaEntity;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.AccountJpaRepository;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaEntity;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaRepository;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.out.UserPersistencePort;
import org.pageflow.global.api.code.ApiCode3;
import org.pageflow.shared.annotation.PersistenceAdapter;
import org.pageflow.shared.jpa.RequiredDataNotFoundException;
import org.pageflow.shared.type.TSID;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : sechan
 */
@PersistenceAdapter
@Transactional
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort, AccountPersistencePort, LoadSessionUserAcl {
    private final AccountJpaRepository accountJpaRepository;
    private final ProfileJpaRepository profileJpaRepository;



    @Override
    public User saveUser(User user) {
        AccountJpaEntity a = accountJpaRepository.findWithProfileById(user.getUid().toLong())
            .orElseThrow(() -> RequiredDataNotFoundException.of(user.getUid().toLong()));

        a.setEmail(user.getEmail().toString());

        ProfileJpaEntity p = a.getProfile();
        p.setPenname(user.getPenname().toString());
        p.setProfileImageUrl(user.getProfileImageUrl().getValue());

        return user;
    }

    @Override
    public User signup(SignupCmd cmd) {
        Long uid = TSID.Factory.getTsid().toLong();

        // 프로필 생성
        ProfileJpaEntity profile = ProfileJpaEntity.builder()
            .id(uid)
            .penname(cmd.getPenname().toString())
            .profileImageUrl(cmd.getProfileImageUrl().getValue())
            .build();

        // 계정 생성
        AccountJpaEntity account = AccountJpaEntity.builder()
            .id(uid)
            .username(cmd.getUsername().toString())
            .password(cmd.getPassword().toString())
            .email(cmd.getEmail().toString())
            .emailVerified(false)
            .provider(cmd.getProvider())
            .role(cmd.getRole())
            .build();

        // 연관관계의 주인인 Profile이 @MapsId를 사용해서 Account의 PK를 참조하기 때문에, Account가 먼저 영속화 되어야한다.
        AccountJpaEntity savedAccount = accountJpaRepository.persist(account);
        profile.associateAccount(savedAccount);
        ProfileJpaEntity savedProfile = profileJpaRepository.persist(profile);

        return toUser(savedAccount, savedProfile);
    }

    @Override
    public Optional<User> loadUser(UID uid) {
        Optional<AccountJpaEntity> a = accountJpaRepository.findWithProfileById(uid.toLong());
        return a.map(accountJpaEntity -> toUser(accountJpaEntity, accountJpaEntity.getProfile()));
    }

    @Override
    public boolean isUserExistByEmail(Username username) {
        return accountJpaRepository.existsByUsername(username.toString());
    }

    @Override
    public boolean isUserExistByEmail(Email email) {
        return accountJpaRepository.existsByEmail(email.toString());
    }



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
            ).orElseThrow(() -> ApiCode3.DATA_NOT_FOUND.feedback("사용자를 찾을 수 없습니다"));
    }

    @Override
    public Account saveAccount(Account account) {
        AccountJpaEntity entity = accountJpaRepository.findById(account.getUid().toLong())
            .orElseThrow(() -> ApiCode3.DATA_NOT_FOUND.feedback("사용자를 찾을 수 없습니다"));
        entity.setRole(account.getRole());
        entity.setPassword(account.getPassword().toString());
        entity.setEmailVerified(account.isEmailVerified());
        return account;
    }



    private Account toAccount(AccountJpaEntity entity) {
        return new Account(
            UID.from(entity.getId()),
            entity.getUsername(),
            EncryptedPassword.of(entity.getPassword()),
            Email.from(entity.getEmail()),
            entity.isEmailVerified(),
            entity.getRole()
        );
    }

    private User toUser(AccountJpaEntity a, ProfileJpaEntity p){
        return new User(
            UID.from(a.getId()),
            Username.of(a.getUsername()),
            a.getRole(),
            Email.from(a.getEmail()),
            a.isEmailVerified(),
            Penname.from(p.getPenname()),
            ProfileImageUrl.from(p.getProfileImageUrl())
        );
    }
}
