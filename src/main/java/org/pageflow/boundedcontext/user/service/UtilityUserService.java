package org.pageflow.boundedcontext.user.service;


import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.constants.UserFetchDepth;
import org.pageflow.boundedcontext.user.constants.UserSignupPolicy;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.boundedcontext.user.model.user.User;
import org.pageflow.boundedcontext.user.repository.AccountRepo;
import org.pageflow.boundedcontext.user.repository.ProfileRepo;
import org.pageflow.global.api.BizException;
import org.pageflow.global.api.code.UserCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * $로 시작하는 서비스는 다른 서비스에서 참조할 수 있는 유틸리티 서비스임을 나타낸다.
 */
@Service
@RequiredArgsConstructor
public class UtilityUserService {

    private final AccountRepo accountRepo;
    private final ProfileRepo profileRepo;
    
    
    /**
     * @throws BizException INVALID_USERNAME, DUPLICATED_USERNAME, USERNAME_CONTAINS_FORBIDDEN_WORD
     */
    public void validateUsername(String username) {
        
        // 1. null, 공백문자 검사
        if(!StringUtils.hasText(username)){
            throw BizException.builder()
                    .code(UserCode.INVALID_USERNAME)
                    .message("비어있는 username; null or 빈 문자열 or 공백 문자열")
                    .build();
        }

        // 2. username 정규식 검사
        if(!username.matches(UserSignupPolicy.USERNAME_REGEX)) {
            throw BizException.builder()
                    .code(UserCode.INVALID_USERNAME)
                    .message(UserSignupPolicy.USERNAME_REGEX_DISCRIPTION)
                    .build();
        }
        
        // 3. 사용할 수 없는 username 검사
        for(String forbiddenWord : UserSignupPolicy.FORBIDDEN_USERNAME_WORDS){
            if(username.contains(forbiddenWord)){
                throw BizException.builder()
                    .code(UserCode.USERNAME_CONTAINS_FORBIDDEN_WORD)
                    .data(forbiddenWord)
                    .build();
            }
        }
        
        // 4. username 중복 검사
        if(accountRepo.existsByUsername(username)){
            throw new BizException(UserCode.DUPLICATED_USERNAME);
        }
    }
    
    /**
     * @throws BizException INVALID_EMAIL, DUPLICATED_EMAIL
     */
    public void validateEmail(String email) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(email)){
            throw BizException.builder()
                .code(UserCode.INVALID_EMAIL)
                .message("비어있는 email; null/빈 문자열/공백 문자열")
                .build();
        }
        
        // 2. email 형식 검사
        if(!email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$")){
            throw BizException.builder()
                .code(UserCode.INVALID_EMAIL)
                .message("email 형식 오류; 정규식 불일치")
                .build();
        }
        
        // 3. email 중복 검사
        if(accountRepo.existsByEmailAndEmailVerified(email, true)){
            throw new BizException(UserCode.DUPLICATED_EMAIL);
        }
    }
    
    /**
     * @throws BizException INVALID_PASSWORD
     */
    public void validatePassword(String password) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(password)){
            throw BizException.builder()
                .code(UserCode.INVALID_PASSWORD)
                .message("비어있는 password; null/빈 문자열/공백 문자열")
                .build();
        }
        
        // 2. password 정규식 검사
        if(!password.matches(UserSignupPolicy.PASSWORD_REGEX)) {
            throw BizException.builder()
                .code(UserCode.INVALID_PASSWORD)
                .message(UserSignupPolicy.PASSWORD_REGEX_DISCRIPTION)
                .build();
        }
    }
    
    /**
     * @throws BizException INVALID_PENNAME, DUPLICATED_PENNAME, PENNAME_CONTAINS_FORBIDDEN_WORD
     */
    public void validatePenname(String penname) {
        
        // 1. null, 빈 문자열 검사
        if(!StringUtils.hasText(penname)){
            throw BizException.builder()
                .code(UserCode.INVALID_PENNAME)
                .message("비어있는 penname; null/빈 문자열/공백 문자열")
                .build();
        }
        
        // 2. penname 정규식 검사
        if(!penname.matches(UserSignupPolicy.PENNAME_REGEX)) {
            throw BizException.builder()
                .code(UserCode.INVALID_PENNAME)
                .message(UserSignupPolicy.PENNAME_REGEX_DISCRIPTION)
                .build();
        }
        
        // 3. 사용할 수 없는 필명
        for(String forbiddenWord : UserSignupPolicy.FORBIDDEN_PENNAME_WORDS) {
            if(penname.contains(forbiddenWord)) {
                throw BizException.builder()
                    .code(UserCode.PENNAME_CONTAINS_FORBIDDEN_WORD)
                    .data(forbiddenWord)
                    .build();
            }
        }
        
        // 4. penname 중복 검사
        if(profileRepo.existsByPenname(penname)){
            throw new BizException(UserCode.DUPLICATED_PENNAME);
        }
    }

    /**
     * @param uid uid
     * @param fetchDepth 프로필 조회 깊이 {@link UserFetchDepth}
     * @return 지정한 수준까지 초기화된 후, JPA 세션이 닫힌 상태의 Profile 인스턴스
     */
    public User fetchUser(Long uid, UserFetchDepth fetchDepth){
        Preconditions.checkNotNull(uid, "uid must not be null");
        Preconditions.checkNotNull(fetchDepth, "fetchDepth must not be null");

        return switch(fetchDepth) {
            case PROXY -> new User(UserFetchDepth.PROXY, accountRepo.getReferenceById(uid), profileRepo.getReferenceById(uid));
            case PROFILE -> new User(UserFetchDepth.PROFILE, accountRepo.getReferenceById(uid), profileRepo.findById(uid).get());
            case ACCOUNT -> new User(UserFetchDepth.ACCOUNT, accountRepo.findById(uid).orElseThrow(), profileRepo.getReferenceById(uid));
            case FULL -> {
                Profile profile = profileRepo.findWithAccountByUid(uid);
                yield new User(UserFetchDepth.FULL, profile.getAccount(), profile);
            }
        };
    }
}
