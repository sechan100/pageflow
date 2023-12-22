package org.pageflow.domain.user.model.dto;


import lombok.Data;
import org.pageflow.domain.user.constants.ProviderType;
import org.pageflow.domain.user.constants.RoleType;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;


/**
 * Account 엔티티와 Profile 엔티티의 정보를 종합하고, 민감한 정보는 제외한 사용자 dto 클래스
 * PrincipalContext에서 현재 로그인중인 사용자의 세션데이터를 저장하는데에 사용
 * @see PrincipalContext
 */
@Data
public class UserDto {
    
    // Account 엔티티 소유
    private Long id;
    private ProviderType provider;
    private String username;
    private String email;
    private boolean emailVerified;
    private RoleType role;
    
    // Profile 엔티티 소유
    private String penname;
    private String profileImgUrl;
    
    
    
    
    public static UserDto from(Account account) {
        UserDto userDto = new UserDto();
        
        userDto.id = account.getId();
        userDto.provider = account.getProvider();
        userDto.username = account.getUsername();
        userDto.email = account.getEmail();
        userDto.emailVerified = account.isEmailVerified();
        userDto.role = account.getRole();
        
        Profile profile = account.getProfile();
        if (profile != null) {
            userDto.penname = profile.getPenname();
            userDto.profileImgUrl = profile.getProfileImgUrl();
        }
        
        return userDto;
    }
    
    public static UserDto from(Profile profile){
        return UserDto.from(profile.getAccount());
    }

    public static UserDto anonymous() {
        UserDto anonymousUserDto = new UserDto();

        anonymousUserDto.username ="anonymous";
        anonymousUserDto.penname = "anonymous";
        anonymousUserDto.role = RoleType.ROLE_ANONYMOUS;
        return anonymousUserDto;
    }
}
