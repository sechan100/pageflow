package org.pageflow.domain.user.model.dto;


import lombok.Data;
import org.pageflow.domain.user.constants.Role;
import org.pageflow.domain.user.entity.Account;


/**
 * 예민한 정보를 제외하고, 세션 관리를 위한 유틸성 필드를 추가한 사용자 세션 데이터.
 * Spring security Principal 규격의 사용자 구현인 PrincipalContext에서 참조한다.
 * @see PrincipalContext
 */
@Data
public class UserSession {
    
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String role;
    private boolean login = false;
    private boolean admin = false;
    
    
    public UserSession(){}
    
    public UserSession(Account account){
        
        this.id = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.role = account.getRole();
        
        if(account.getProfile() != null){
            this.nickname = account.getProfile().getNickname();
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
