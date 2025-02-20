package org.pageflow.user.adapter.in.auth;

import lombok.Getter;
import org.pageflow.common.api.RequestContextUserPrincipal;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.utility.Forward;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * <p>
 *   Spring security의 UserDetails와 OAuth2User의 구현체.
 *   SpringSecurity가 인증만 처리해주면 추가적인 처리나 사용자 응답을 위한 추가 로직이 필요한데,
 *   그 추가로직으로 가기위한 forward 객체까지 함께 담고있는 Principal.
 * </p>
 * <p>
 *   로그인시에 FormLogin과 OAuth2Login의 Principal 반환타입 규격을 모두 충족시키기 위해 사용된다.
 *   해당 객체는 최초 login하여 session을 발급할 때만 사용되고, 이후의 session에서 정보를 가져올 때는
 *   {@link RequestContextUserPrincipal}의 구현체를 사용해야한다.
 * </p>
 * <p>
 *  해당 객체는 {@link Forward} 객체를 가지고있는데,
 *  이는 {@link SuccessAuthenticatonForwardHandler}에서 forward된다.
 *  SpringSecurity는 인증로직만을 담당하고, 이후 토큰을 반환하거나, OAuth2 PreSignup을 위한 로직은
 *  적절한 Controller로 위임되어 처리되기 때문에, 해당 객체는 다음 처리자로 forward하기 위한 객체를 가지고 있는 것이다.
 *  다만, 어쨌든 SpringSecurity 스펙상 Principal 객체가 반환되어야 인증이 성공한 것으로 처리하기 때문에, 해당 객체를 반환해주기는 해야한다.
 *  주로 다음과 같은 케이스가 있다.
 *  </p>
 *  <ul>
 *    <li>Form Login 성공 -> Token을 반환하는 Endpoint로 forward</li>
 *    <li>OAuth2 Login 성공 -> Token을 반환하는 Endpoint로 forward</li>
 *    <li>OAuth2로 회원가입이 필요한 경우 -> OAuth2PreSignup을 처리하는 Endpoint로 forward</li>
 *  </ul>
 * @author : sechan
 */
public class ForwardRequireAuthenticationPrincipal extends User implements OAuth2User {
  @Getter
  private final Forward forward;

  private ForwardRequireAuthenticationPrincipal(
    Forward forward, String username, String password
  ){
    super(
      username,
      password,
      RoleType.toAuthorities(RoleType.ROLE_ANONYMOUS)
    );
    this.forward = forward;
  }

  /**
   * form login에서 이후에 추가적으로 password 검증이 필요할 때, username과 password를 함께 전달하기 위해서 사용한다.
   * @param forward
   * @param username
   * @param password
   * @return
   */
  public static ForwardRequireAuthenticationPrincipal form(Forward forward, String username, String password){
    return new ForwardRequireAuthenticationPrincipal(forward, username, password);
  }

  /**
   * 추가적인 비밀번호 검증이 필요없을 때 사용
   * @param forward
   * @return
   */
  public static ForwardRequireAuthenticationPrincipal oAuth2(Forward forward){
    return new ForwardRequireAuthenticationPrincipal(
      forward,
      "ForwardRequireAuthenticationPrincipal.dummyUsername",
      "ForwardRequireAuthenticationPrincipal.dummyPassword"
    );
  }



  @Override
  public Map<String, Object> getAttributes() {
    throw new UnsupportedOperationException("해당 클래스는 타입 호환을 위한 Wrapper입니다. " +
      "해당 메소드는 타입 호환을 위해 구현한 OAuth2User의 스펙이며, 해당 구현에서는 이를 지원하지 않습니다.");
  }

  @Override
  public String getName() {
    return getUsername();
  }

}
