package org.pageflow.boundedcontext.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.auth.port.in.EmailVerificationUseCase;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.file.model.FilePath;
import org.pageflow.boundedcontext.file.model.FileUploadCmd;
import org.pageflow.boundedcontext.file.service.FileService;
import org.pageflow.boundedcontext.file.shared.FileType;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.port.in.ProfileImageFile;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.port.out.CheckForbiddenWordPort;
import org.pageflow.boundedcontext.user.port.out.UserPersistencePort;
import org.pageflow.global.api.code.Code3;
import org.pageflow.global.api.code.Code4;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final UserPersistencePort userPersistePort;
    private final CheckForbiddenWordPort forbiddenWordPort;
    private final EmailVerificationUseCase emailVerificationUseCase;
    private final FileService fileService;



    /**
     * <P>OAuth2로 회원가입을 하게되면, 요청이 2번에 걸쳐서 처리된다.</P>
     *
     * <P>따라서 ProviderType과 RoleType과 같은 중요한 데이터들은,
     * 사용자가 수정할 수 없도록 서버측에서 저장하여 관리할 필요가 있다.</P>
     *
     * <P>서버는 이를 cache에 저장한다. 만약 username으로 캐싱된 회원가입 데이터가 존재하는 경우,
     * 해당 요청을 OAuth2를 통한 회원가입으로 간주하고, 캐시에 저장된 데이터와 사용자 입력데이터를 적절히 혼합하여 회원가입을 진행한다.
     * </P>
     */
    @Override
    public UserDto.Default signup(SignupCmd cmd) {
        // 중복 검사
        checkUniqueUsername(cmd.getUsername());
        checkUniqueEmail(cmd.getEmail());

        // 금지어 검사
        forbiddenWordPort.checkPennameAnyContains(cmd.getPenname());
        forbiddenWordPort.checkUsernameAnyContains(cmd.getUsername());

        /* REVIEW: input port로 쓰인 SignupCmd를 도메인 모델로의 변환없이 그대로 out port로 내보내고있다.
         * signup시에는 auth 도메인과 같은 곳에서 사용될 데이터들도 어쩔 수 없이 같이 저장하야하지만, 해당 bounded에서는
         * 이러한 데이터들은 굳이 필요하지 않아서 핵심 도메인 모델에 없다.
         * 때문에 SignupCmd와의 불일치가 발생한다. 하지만 이것만을 위해서 따로 signup 전용 도메인 객체를 만들기에는
         * 로직이 간단하다.
         */
        User user = userPersistePort.signup(cmd);
        return toDto(user);
    }

    @Override
    public UserDto.Default changeEmail(UID uid, Email email) {
        checkUniqueEmail(email);
        User user = load(uid);
        user.changeEmail(email);
        userPersistePort.saveUser(user);
        emailVerificationUseCase.unverify(uid);
        return toDto(user);
    }

    @Override
    public UserDto.Default changePenname(UID uid, Penname penname) {
        forbiddenWordPort.checkPennameAnyContains(penname);
        User user = load(uid);
        user.changePenname(penname);
        userPersistePort.saveUser(user);
        return toDto(user);
    }

    @Override
    public UserDto.Default changeProfileImage(UID uid, ProfileImageFile file) {
        User user = load(uid);
        ProfileImageUrl oldImageUrl = user.getProfileImageUrl();
        // 외부서버 이미지가 아닌 경우, 기존 이미지를 삭제
        if(oldImageUrl.isInternalImage()){
            fileService.delete(oldImageUrl.getValue());
        }
        // 새 이미지 업로드
        FileUploadCmd cmd = new FileUploadCmd(
            uid.getValue(),
            FileType.USER.PROFILE_IMAGE,
            file.getValue()
        );
        FilePath path = fileService.upload(cmd);
        // 도메인 변경
        user.changeProfileImageUrl(ProfileImageUrl.of(path.getWebUri()));
        userPersistePort.saveUser(user);
        return toDto(user);
    }



    private User load(UID uid){
        return userPersistePort.loadUser(uid).orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("사용자를 찾을 수 없습니다."));
    }

    private void checkUniqueUsername(Username username){
        if(userPersistePort.isUserExistByEmail(username)){
            throw Code4.UNIQUE_FIELD_DUPLICATED
                .feedback(t -> t.getUsername_duplicate());
        }
    }

    private void checkUniqueEmail(Email email) {
        if(userPersistePort.isUserExistByEmail(Email.of(email.getValue()))){
            throw Code4.UNIQUE_FIELD_DUPLICATED
                .feedback(t -> t.getEmail_duplicate());
        }
    }

    private UserDto.Default toDto(User user){
        return new UserDto.Default(
            user.getUid().getValue(),
            user.getUsername().toString(),
            user.getEmail().toString(),
            user.isEmailVerified(),
            user.getRole(),
            user.getPenname().toString(),
            user.getProfileImageUrl().toString()
        );
    }

}
