package org.pageflow.domain.user.model.dto;


import lombok.Data;
import org.pageflow.domain.user.constants.Role;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;


/**
 * 예민한 정보를 제외하고, 세션 관리를 위한 유틸성 필드를 추가한 사용자 세션 데이터.
 * Spring security Principal 규격의 사용자 구현인 PrincipalContext에서 참조한다.
 * @see PrincipalContext
 */
@Data
public class UserSession {
    
    /**
     * 사용자가 세션에 가지고있어야할 필드를 정의한다.
     * 필요에 따라 자유롭게 추가해도 된다.
     * 추가 후 아래 Account를 매개받는 생성자에서 필드를 적절히 초기화 시켜주면된다.
     */
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String role;
    private String profileImgUrl;
    private boolean login = false;
    private boolean admin = false;
    
    
    public UserSession(){}
    
    /**
     * 실제로 세션에 저장되는 사용자 정보는, 엔티티가 아닌 해당 클래스를 사용한다.
     * 이 때, 사용자의 데이터는 해당 생성자를 통해서 가져와 매핑한다.
     */
    public UserSession(Account account){
        
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.role = account.getRole();
        
        Profile profile = account.getProfile();
        if(profile != null){
            this.nickname = profile.getNickname();
            this.profileImgUrl = profile.getProfileImgUrl();
        }
        
        if(this.role.equals(Role.ADMIN)) {
            admin = true;
        }
    }
    
    public static UserSession anonymousUserSession(){
        UserSession anonymousUserSession = new UserSession();
        
        anonymousUserSession.setUsername("anonymous");
        anonymousUserSession.setNickname("anonymous");
        anonymousUserSession.setRole(Role.ANONYMOUS);
        return anonymousUserSession;
    }
}
