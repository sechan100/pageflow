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
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.port.in.ProfileImageFile;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.global.api.ApiAccess;
import org.pageflow.global.api.ApiResponse;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.api.code.ApiCode2;
import org.pageflow.global.filter.UriPrefix;
import org.pageflow.global.property.AppProps;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserWebAdapter {
    private final AppProps props;
    private final RequestContext requestContext;
    private final OAuth2PresignupRedisRepo preSignupRepo;
    private final UserUseCase userUsecase;



    @Secured(ApiAccess.ANONYMOUS)
    @Operation(summary = "회원가입", description = "새로운 사용자의 회원가입을 요청")
    @PostMapping("/signup")
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
            Username.of(form.getUsername()),
            Password.encrypt(form.getPassword()),
            Email.from(form.getEmail()),
            Penname.from(form.getPenname()),
            RoleType.ROLE_USER,
            provider,
            ProfileImageUrl.from(profileImageUrl)
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
    @Hidden
    @GetMapping(PRE_SIGNUP_PATH)
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

        return ApiResponse.withoutFeedback(
            ApiCode2.OAUTH2_SIGNUP_REQUIRED,
            result
        );
    }

    @PostMapping("/user/profile/email")
    public Res.SessionUser changeEmail(@RequestBody Map<String, String> form){ // "email"
        UID uid = requestContext.getUid();
        Email email = Email.from(form.get("email"));

        UserDto.User result = userUsecase.changeEmail(uid, email);
        return toSessionUser(result);
    }

    @PostMapping("/user/profile/penname")
    public Res.SessionUser changePenname(@RequestBody Map<String, String> form){ // "penname"
        UID uid = requestContext.getUid();
        Penname penname = Penname.from(form.get("penname"));

        UserDto.User result = userUsecase.changePenname(uid, penname);
        return toSessionUser(result);
    }

    /**
     * 프로필 이미지를 서버에 업로드하고, 로그인 중인 사용자의 ProfileImageUrl을 변경한다.
     * @param file 프로필 이미지 파일
     */
    @PostMapping("/user/profile/profile-image")
    public Res.SessionUser changeProfileImage(@RequestPart MultipartFile file){
        UID uid = requestContext.getUid();
        ProfileImageFile profileImageFile = ProfileImageFile.of(file);

        UserDto.User result = userUsecase.changeProfileImage(uid, profileImageFile);
        return toSessionUser(result);
    }

    /**
     * 로그인 중인 사용자의 ProfileImageUrl을 기본 이미지로 변경한다.
     */
    @DeleteMapping("/user/profile/profile-image")
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
