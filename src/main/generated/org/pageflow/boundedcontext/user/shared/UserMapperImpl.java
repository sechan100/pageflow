package org.pageflow.boundedcontext.user.shared;

import javax.annotation.processing.Generated;
import org.pageflow.boundedcontext.user.adapter.in.web.UserRes;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.Email;
import org.pageflow.boundedcontext.user.domain.Penname;
import org.pageflow.boundedcontext.user.domain.ProfileImage;
import org.pageflow.boundedcontext.user.domain.UID;
import org.pageflow.boundedcontext.user.domain.Username;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-25T17:36:10+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.8 (Homebrew)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto.Signup signupDto_UidAndsignupCmd(UID uid, SignupCmd cmd) {
        if ( uid == null && cmd == null ) {
            return null;
        }

        UserDto.Signup.SignupBuilder signup = UserDto.Signup.builder();

        if ( cmd != null ) {
            signup.username( cmdUsernameValue( cmd ) );
            signup.email( cmdEmailValue( cmd ) );
            signup.penname( cmdPennameValue( cmd ) );
            signup.profileImageUrl( cmdProfileImageValue( cmd ) );
            signup.provider( cmd.getProvider() );
            signup.role( cmd.getRole() );
        }
        signup.isEmailVerified( cmd.getEmail().isVerified() );
        signup.uid( uid.getValue() );

        return signup.build();
    }

    @Override
    public UserRes.Signup resSignup_dtoSignup(UserDto.Signup dto) {
        if ( dto == null ) {
            return null;
        }

        UserRes.Signup.SignupBuilder signup = UserRes.Signup.builder();

        signup.username( dto.getUsername() );
        signup.email( dto.getEmail() );
        signup.penname( dto.getPenname() );

        return signup.build();
    }

    private String cmdUsernameValue(SignupCmd signupCmd) {
        if ( signupCmd == null ) {
            return null;
        }
        Username username = signupCmd.getUsername();
        if ( username == null ) {
            return null;
        }
        String value = username.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private String cmdEmailValue(SignupCmd signupCmd) {
        if ( signupCmd == null ) {
            return null;
        }
        Email email = signupCmd.getEmail();
        if ( email == null ) {
            return null;
        }
        String value = email.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private String cmdPennameValue(SignupCmd signupCmd) {
        if ( signupCmd == null ) {
            return null;
        }
        Penname penname = signupCmd.getPenname();
        if ( penname == null ) {
            return null;
        }
        String value = penname.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private String cmdProfileImageValue(SignupCmd signupCmd) {
        if ( signupCmd == null ) {
            return null;
        }
        ProfileImage profileImage = signupCmd.getProfileImage();
        if ( profileImage == null ) {
            return null;
        }
        String value = profileImage.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }
}
