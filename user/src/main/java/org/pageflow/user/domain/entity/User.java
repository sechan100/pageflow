package org.pageflow.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.Password;

import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(
  name = "users",
  indexes = {
    @Index(name = "idx_account_username", columnList = "username")
  }
)
public class User extends BaseJpaEntity {

  @Id
  @Column(updatable = false, nullable = false)
  private UUID id;

  @Getter
  @Column(unique = true, nullable = false, updatable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Getter
  @Column(name = "email", nullable = false)
  private String email;

  @Getter
  @Column(name = "is_email_verified", nullable = false)
  private Boolean isEmailVerified;

  @Getter
  @Column(nullable = false)
  private String penname;

  @Getter
  @Column(nullable = false, columnDefinition = "VARCHAR(255)")
  private String profileImageUrl;

  @Getter
  @Column(nullable = false, columnDefinition = "TEXT")
  private String bio;

  /**
   * NATIVE, GOOGLE, KAKAO, NAVER, GITHUB
   * 영속화시 null이라면 NATIVE로 초기화한다.
   */
  @Getter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private ProviderType provider;

  /**
   * ROLE_ADMIN, ROLE_MANAGER, ROLE_USER, ROLE_ANONYMOUS
   */
  @Getter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleType role;

  public static User create(
    UUID id,
    String username,
    Password password,
    String email,
    String penname,
    String profileImageUrl,
    ProviderType provider,
    RoleType role
  ) {
    return new User(
      id,
      username,
      password.getValue(),
      email,
      false,
      penname,
      profileImageUrl,
      "",
      provider,
      role
    );
  }


  public UID getUid() {
    return new UID(id);
  }

  /**
   * password를 노출시키지 않도록 주의해서 사용
   *
   * @return
   */
  public String _getPassword() {
    return password;
  }

  /**
   * @param currentPassword
   * @param password
   * @return Result
   * - BAD_CREDENTIALS: currentPassword가 일치하지 않는 경우
   * - PASSWORD_SAME_AS_BEFORE: 새로운 비밀번호가 기존 비밀번호와 동일한 경우
   */
  public Result<Void> changePassword(String currentPassword, Password password) {
    if(!passwordMatches(currentPassword)) {
      return Result.unit(UserCode.BAD_CREDENTIALS);
    }
    if(currentPassword.equals(password.getValue())) {
      return Result.unit(UserCode.PASSWORD_SAME_AS_BEFORE);
    }
    this.password = password.getValue();
    return Result.ok();
  }

  public boolean passwordMatches(String rawPassword) {
    return Password.matches(rawPassword, this.password);
  }

  /**
   * 언제나 이메일의 인증과 변경은 함께 이루어진다.
   */
  public void verifyAndChangeEmail(String email) {
    this.email = email;
    this.isEmailVerified = true;
  }

  public void changePenname(String penname) {
    this.penname = penname;
  }

  public void changeProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }
}
