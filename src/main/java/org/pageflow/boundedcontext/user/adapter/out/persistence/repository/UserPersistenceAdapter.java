package org.pageflow.boundedcontext.user.adapter.out.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.AccountJpaEntity;
import org.pageflow.boundedcontext.user.adapter.out.persistence.entity.ProfileJpaEntity;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.out.CmdUserPort;
import org.pageflow.boundedcontext.user.port.out.LoadUserPort;
import org.pageflow.boundedcontext.user.port.out.UserExistenceCheckPort;
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
public class UserPersistenceAdapter implements CmdUserPort, LoadUserPort, UserExistenceCheckPort {
    private final AccountJpaRepository accountJpaRepo;
    private final ProfileJpaRepository profileJpaRepo;


    @Override
    public void save(User user) {
        AccountJpaEntity a = accountJpaRepo.findWithProfileById(user.getUid().toLong())
            .orElseThrow(()-> RequiredDataNotFoundException.of(user.getUid().toLong()));

        a.setUsername(user.getUsername().toString());
        a.setEmail(user.getEmail().toString());
        a.setEmailVerified(user.getEmail().isVerified());

        ProfileJpaEntity p = a.getProfile();
        p.setPenname(user.getPenname().toString());
        p.setProfileImageUrl(user.getProfileImage().getValue());
    }

    @Override
    public User signup(SignupCmd cmd) {
        Long uid = TSID.Factory.getTsid().toLong();

        // 프로필 생성
        ProfileJpaEntity profile = ProfileJpaEntity.builder()
            .id(uid)
            .penname(cmd.getPenname().toString())
            .profileImageUrl(cmd.getProfileImage().getValue())
            .build();

        // 계정 생성
        AccountJpaEntity account = AccountJpaEntity.builder()
            .id(uid)
            .username(cmd.getUsername().toString())
            .password(cmd.getPassword().toString())
            .email(cmd.getEmail().toString())
            .emailVerified(cmd.getEmail().isVerified())
            .provider(cmd.getProvider())
            .role(cmd.getRole())
            .build();

        // 연관관계의 주인인 Profile이 @MapsId를 사용해서 Account의 PK를 참조하기 때문에, Account가 먼저 영속화 되어야한다.
        AccountJpaEntity savedAccount = accountJpaRepo.persist(account);
        profile.associateAccount(savedAccount);
        ProfileJpaEntity savedProfile = profileJpaRepo.persist(profile);

        return toDomain(savedAccount, savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> load(UID uid) {
        Optional<AccountJpaEntity> a = accountJpaRepo.findWithProfileById(uid.toLong());
        return a.map(accountJpaEntity -> toDomain(accountJpaEntity, accountJpaEntity.getProfile()));
    }

    @Override
    public boolean isExist(Username username) {
        return accountJpaRepo.existsByUsername(username.toString());
    }

    @Override
    public boolean isExist(Email email) {
        return accountJpaRepo.existsByEmailAndEmailVerified(email.toString(), email.isVerified());
    }


    private User toDomain(AccountJpaEntity a, ProfileJpaEntity p){
        return new User(
            UID.from(a.getId()),
            Username.of(a.getUsername()),
            Email.of(a.getEmail(), a.isEmailVerified()),
            Penname.of(p.getPenname()),
            ProfileImage.of(p.getProfileImageUrl())
        );
    }
}
