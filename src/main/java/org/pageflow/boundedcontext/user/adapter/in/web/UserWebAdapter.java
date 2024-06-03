package org.pageflow.boundedcontext.user.adapter.in.web;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.auth.springsecurity.common.InAuthingInFilterForwardFactory;
import org.pageflow.boundedcontext.auth.springsecurity.oauth2.owner.OAuth2ResourceOwner;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.cache.entity.OAuth2PreSignupCache;
import org.pageflow.boundedcontext.user.adapter.out.cache.repository.OAuth2PresignupRedisRepo;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.boundedcontext.user.domain.Penname;
import org.pageflow.boundedcontext.user.domain.ProfileImageFile;
import org.pageflow.boundedcontext.user.domain.ProfileImageUrl;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.global.api.ApiAccess;
import org.pageflow.global.api.ApiResponse;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.api.code.ApiCode2;
import org.pageflow.global.filter.UriPrefix;
import org.pageflow.global.property.AppProps;
import org.pageflow.shared.annotation.web.Delete;
import org.pageflow.shared.annotation.web.Get;
import org.pageflow.shared.annotation.web.Post;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserWebAdapter {
    private final AppProps props;
    private final RequestContext requestContext;
    private final OAuth2PresignupRedisRepo preSignupRepo;
    private final UserUseCase userUsecase;



    @Post(value = "/signup", access = ApiAccess.ANONYMOUS)
    @Operation(summary = "회원가입", description = "새로운 사용자의 회원가입을 요청")
    public Res.Signup signup(@Valid @RequestBody Req.SignupForm form) {
        Optional<OAuth2PreSignupCache> loaded = preSignupRepo.findById(form.getUsername());

        String profileImageUrl;
        ProviderType provider;
        if(loaded.isPresent()){
            OAuth2PreSignupCache cache = loaded.get();
            provider = cache.getProvider(); // not NATIVE
            profileImageUrl = cache.getProfileImageUrl();
            preSignupRepo.delete(cache);
        } else {
            provider = ProviderType.NATIVE;
            profileImageUrl = props.user.defaultProfileImageUrl;
        }

        SignupCmd cmd = new SignupCmd(
            form.getUsername(),
            form.getPassword(),
            form.getEmail(),
            form.getPenname(),
            RoleType.ROLE_USER,
            provider,
            profileImageUrl
        );

        UserDto.User result = userUsecase.signup(cmd);
        return new Res.Signup(
            result.getUsername(),
            result.getEmail(),
            result.getPenname()
        );
    }

    public static final String PRE_SIGNUP_PATH = UriPrefix.PRIVATE + "/oauth2/pre-signup";
    public static final String RESOURCE_OWNER_REQUEST_ATTR_KEY = "resourceOwner";
    /**
     * {@link InAuthingInFilterForwardFactory#getOAuth2PreSignupForward(OAuth2ResourceOwner)}
     */
    @Get(value = PRE_SIGNUP_PATH, access = ApiAccess.ANONYMOUS)
    @Hidden
    public ApiResponse<Res.PreSignuped> preSignup() {
        OAuth2ResourceOwner owner = requestContext.getRequestAttr(RESOURCE_OWNER_REQUEST_ATTR_KEY);
        boolean isAlreadyPreSignuped = preSignupRepo.existsById(owner.getUsername());

        // pre-signup 기록이 없다면 저장
        if(!isAlreadyPreSignuped){
            preSignupRepo.save(OAuth2PreSignupCache.builder()
                .username(owner.getUsername())
                .provider(owner.getProviderType())
                .profileImageUrl(owner.getProfileImgUrl())
                .build()
            );
        }

        Res.PreSignuped result = new Res.PreSignuped(
            owner.getUsername(),
            owner.getEmail(),
            owner.getNickname()
        );

        return new ApiResponse(ApiCode2.OAUTH2_SIGNUP_REQUIRED, result);
    }

    @Post("/user/profile/email")
    public Res.SessionUser changeEmail(@RequestBody Req.Email form){
        UID uid = requestContext.getUid();
        Email email = Email.from(form.getEmail());

        UserDto.User result = userUsecase.changeEmail(uid, email);
        return toSessionUser(result);
    }

    @Post("/user/profile/penname")
    public Res.SessionUser changePenname(@RequestBody Req.Penname form){ // "penname"
        UID uid = requestContext.getUid();
        Penname penname = Penname.from(form.getPenname());

        UserDto.User result = userUsecase.changePenname(uid, penname);
        return toSessionUser(result);
    }

    /**
     * 프로필 이미지를 서버에 업로드하고, 로그인 중인 사용자의 ProfileImageUrl을 변경한다.
     * @param file 프로필 이미지 파일
     */
    @Post("/user/profile/profile-image")
    public Res.SessionUser changeProfileImage(@RequestPart MultipartFile file){
        UID uid = requestContext.getUid();
        ProfileImageFile profileImageFile = ProfileImageFile.of(file);

        UserDto.User result = userUsecase.changeProfileImage(uid, profileImageFile);
        return toSessionUser(result);
    }

    /**
     * 로그인 중인 사용자의 ProfileImageUrl을 기본 이미지로 변경한다.
     */
    @Delete("/user/profile/profile-image")
    public Res.SessionUser changeProfileImageToDefault(){
        UID uid = requestContext.getUid();
        ProfileImageUrl profileImageUrl = ProfileImageUrl.from(props.user.defaultProfileImageUrl);

        UserDto.User result = userUsecase.changeProfileImage(uid, profileImageUrl);
        return toSessionUser(result);
    }



    private Res.SessionUser toSessionUser(UserDto.User user){
        return new Res.SessionUser(
            user.getUid(),
            user.getUsername(),
            user.getEmail(),
            user.isEmailVerified(),
            user.getRole(),
            user.getPenname(),
            user.getProfileImageUrl()
        );
    }
}
