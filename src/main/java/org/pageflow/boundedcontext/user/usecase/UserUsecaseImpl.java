package org.pageflow.boundedcontext.user.usecase;

import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.cache.OAuth2PresignupCache;
import org.pageflow.boundedcontext.user.constants.ProviderType;
import org.pageflow.boundedcontext.user.constants.RoleType;
import org.pageflow.boundedcontext.user.domain.User;
import org.pageflow.boundedcontext.user.dto.AccountDto;
import org.pageflow.boundedcontext.user.dto.ApiRevealSignupForm;
import org.pageflow.boundedcontext.user.dto.ProfileDto;
import org.pageflow.boundedcontext.user.dto.SignupResult;
import org.pageflow.boundedcontext.user.mapper.UserMapper;
import org.pageflow.boundedcontext.user.repository.OAuth2PresignupCacheRepository;
import org.pageflow.boundedcontext.user.service.UserQueries;
import org.pageflow.shared.annotation.CommandService;
import org.pageflow.shared.data.query.TryQuery;

import java.util.UUID;

/**
 * @author : sechan
 */
@CommandService
@RequiredArgsConstructor
public class UserUsecaseImpl implements UserUsecase {

    private final OAuth2PresignupCacheRepository presignupRepo;
    private final UserQueries queries;
    private final UserMapper mapper;

    /**
     * <P>OAuth2로 회원가입을 하게되면, 요청이 2번에 걸쳐서 처리된다.</P>
     *
     * <P>따라서 ProviderType과 RoleType과 같은 중요한 데이터들은,
     * 사용자가 수정할 수 없도록 서버측에서 저장하여 관리할 필요가 있다.</P>
     *
     * <P>서버는 이를 cache에 저장한다. 만약 username으로 캐싱된 회원가입 데이터가 존재하는 경우,
     * 해당 요청을 OAuth2를 통한 회원가입으로 간주하고, 캐시에 저장된 데이터와 사용자 입력데이터를 적절히 혼합하여 회원가입을 진행한다.
     * </P>
     * @param form 회원가입 폼
     * @return 회원가입 결과 dto
     */
    @Override
    public SignupResult signup(ApiRevealSignupForm form){
        boolean isOAuth = presignupRepo.existsById(form.getUsername());

        ProviderType provider;
        // OAuth2
        if(isOAuth){
            OAuth2PresignupCache OAuth2PresignupCache = presignupRepo.findById(form.getUsername()).get();
            presignupRepo.deleteById(form.getUsername());
            provider = OAuth2PresignupCache.getProvider(); // not NATIVE
        // 일반
        } else {
            provider = ProviderType.NATIVE;
        }

        User user = User.signup(
            form,
            provider,
            RoleType.ROLE_USER
        );

        Tuple2<AccountDto, ProfileDto> tuple2 = queries.fetchAccountAndProfile(user.getId());
        return mapper.dtoToSignupResult(tuple2._1(), tuple2._2());
    }

    @Override
    public ApiRevealSignupForm getOauth2PresignupData(String username){
        TryQuery<OAuth2PresignupCache> getCache = TryQuery.of(() -> presignupRepo.findById(username).get());
        OAuth2PresignupCache cache = getCache.findOrElseThrow(
            () -> new IllegalArgumentException("OAuth2 signup 캐시에 저장된 데이터가 존재하지 않습니다.")
        );

        String dummyPassword = UUID.randomUUID().toString();
        return ApiRevealSignupForm.builder()
            .username(cache.getUsername())
            .password(dummyPassword)
            .email(cache.getEmail())
            .penname(cache.getPenname())
            .profileImgUrl(cache.getProfileImgUrl())
            .build();
    }
}
