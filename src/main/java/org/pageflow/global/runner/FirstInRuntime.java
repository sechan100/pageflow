package org.pageflow.global.runner;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.adapter.out.persistence.repository.AccountJpaRepository;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.application.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.application.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.pageflow.global.api.code.ApiCode;
import org.pageflow.global.property.AppProps;
import org.pageflow.shared.utility.JsonUtility;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FirstInRuntime {

    private final UserUseCase userUsecase;
    private final AccountJpaRepository accountJpaRepo;
    private final AppProps props;

    
    @Bean
    public ApplicationRunner runners() {
        return args -> {
            createAdminAccount();
            checkApiCodeDuplication();
        };
    }
    
    @Transactional
    private void createAdminAccount() {
        if(accountJpaRepo.existsByRole(RoleType.ROLE_ADMIN)) {
            return;
        }
        AppProps.Admin admin = props.admin;
        SignupCmd cmd = new SignupCmd(
            Username.of(admin.username),
            Password.encrypt(admin.password),
            Email.ofVerified(admin.email),
            Penname.of(admin.penname),
            RoleType.ROLE_ADMIN,
            ProviderType.NATIVE,
            ProfileImage.of(props.user.defaultProfileImageUri)
        );

        UserDto.Signup result = userUsecase.signup(cmd);

        log.info("관리자 계정을 생성했습니다. {}", JsonUtility.toJson(result));
    }
    

    @SuppressWarnings({"deprecation", "MagicNumber"})
    private void checkApiCodeDuplication() throws Exception {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("org.pageflow.global.api.code"))
            .setScanners(new SubTypesScanner()));
        Set<Class<? extends ApiCode>> classes = reflections.getSubTypesOf(ApiCode.class);

        List<Integer> allCodes = new ArrayList();

        for(Class<? extends ApiCode> clazz : classes) {
            Method allEnumsMethod = clazz.getMethod("values");
            ApiCode[] apiCodeArr = (ApiCode[]) allEnumsMethod.invoke(null);
            for(ApiCode code : apiCodeArr){
                allCodes.add(code.getCode());
            }
        }

        // 형식 검사
        boolean isExistInvalidApiCode = allCodes.stream()
            .anyMatch(code -> code < 1000 || code > 9999);
        if(isExistInvalidApiCode){
            List<Integer> invalidApiCodes = allCodes.stream()
                .filter(code -> code < 1000 || code > 9999)
                .toList();
            throw new ApiCodeFormatException("ApiCode는 1000~9999 사이의 정수여야 합니다:" + invalidApiCodes);
        }

        // 중복 검사

        if(allCodes.stream().distinct().count() != allCodes.size()){
            Set<Integer> seen = new HashSet<>();
            Set<Integer> duplicates = new HashSet<>();
            for (Integer code : allCodes) {
                if(!seen.add(code)) {
                    duplicates.add(code);
                }
            }
            throw new ApiCodeDuplicatedException("중복된 ApiCode가 존재합니다:" + duplicates);
        }

    }
    
    
}