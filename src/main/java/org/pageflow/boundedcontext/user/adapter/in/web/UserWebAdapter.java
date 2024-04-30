package org.pageflow.boundedcontext.user.adapter.in.web;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.auth.springsecurity.common.InAuthingInFilterForwardFactory;
import org.pageflow.boundedcontext.auth.springsecurity.oauth2.owner.OAuth2ResourceOwner;
import org.pageflow.boundedcontext.user.adapter.out.cache.entity.OAuth2PreSignupCache;
import org.pageflow.boundedcontext.user.adapter.out.cache.repository.OAuth2PresignupRedisRepo;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.global.api.ApiAccess;
import org.pageflow.global.api.ApiResponse;
import org.pageflow.global.api.RequestContext;
import org.pageflow.global.api.code.Code2;
import org.pageflow.global.filter.UriPrefix;
import org.pageflow.global.property.AppProps;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public Res.Signup signup(@Valid @RequestBody UserReq.SignupForm form) {
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
            profileImageUrl = props.user.defaultProfileImageUri;
        }

        SignupCmd cmd = new SignupCmd(
            Username.of(form.getUsername()),
            Password.encrypt(form.getPassword()),
            Email.of(form.getEmail()),
            Penname.of(form.getPenname()),
            RoleType.ROLE_USER,
            provider,
            ProfileImageUrl.of(profileImageUrl)
        );

        UserDto.Default result = userUsecase.signup(cmd);
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
            Code2.OAUTH2_SIGNUP_REQUIRED,
            result
        );
    }
}
