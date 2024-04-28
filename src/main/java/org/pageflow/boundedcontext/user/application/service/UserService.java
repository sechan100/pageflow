package org.pageflow.boundedcontext.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.boundedcontext.user.domain.User;
import org.pageflow.boundedcontext.user.domain.Username;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.port.out.CmdUserPort;
import org.pageflow.boundedcontext.user.port.out.LoadUserPort;
import org.pageflow.boundedcontext.user.port.out.PennameForbiddenWordPort;
import org.pageflow.boundedcontext.user.port.out.UserExistenceCheckPort;
import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final LoadUserPort loadUserPort;
    private final CmdUserPort cmdUserPort;
    private final UserExistenceCheckPort isExistPort;
    private final PennameForbiddenWordPort pennameForbiddenWordPort;

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
    public UserDto.Signup signup(SignupCmd cmd) {
        // 중복 검사
        checkUniqueUsername(cmd.getUsername());
        checkUniqueEmail(cmd.getEmail());
        // 필명 금지어 검사
        pennameForbiddenWordPort.validateForbiddenWord(cmd.getPenname());

        /* REVIEW: input port로 쓰인 SignupCmd를 도메인 모델로의 변환없이 그대로 out port로 내보내고있다.
         * signup시에는 auth 도메인과 같은 곳에서 사용될 데이터들도 어쩔 수 없이 같이 저장하야하지만, 해당 bounded에서는
         * 이러한 데이터들은 굳이 필요하지 않아서 핵심 도메인 모델에 없다.
         * 때문에 SignupCmd와의 불일치가 발생한다. 하지만 이것만을 위해서 따로 signup 전용 도메인 객체를 만들기에는
         * 로직이 간단하다.
         */
        User user = cmdUserPort.signup(cmd);
        return toSignupDto(user, cmd);
    }



    private void checkUniqueUsername(Username username){
        if(isExistPort.isExist(username)){
            throw Code4.UNIQUE_FIELD_DUPLICATED
                .feedback(t -> t.getUsername_duplicate());
        }
    }

    private void checkUniqueEmail(Email email){
        if(isExistPort.isExist(Email.of(email.getValue()))){
            throw Code4.UNIQUE_FIELD_DUPLICATED
                .feedback(t -> t.getEmail_duplicate());
        }
    }

    private UserDto.Signup toSignupDto(User user, SignupCmd cmd){
        return new UserDto.Signup(
            user.getUid().getValue(),
            user.getUsername().toString(),
            user.getEmail().toString(),
            user.isEmailVerified(),
            cmd.getProvider(),
            cmd.getRole(),
            user.getPenname().toString(),
            user.getProfileImage().toString()
        );
    }

}
