package org.pageflow.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.common.result.NullData;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.application.UserCode;
import org.pageflow.user.domain.Password;

import java.util.UUID;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(
  name = "account",
  indexes = {
    @Index(name = "idx_account_username", columnList = "username")
  }
)
public class Account extends BaseJpaEntity {

  @Id
  @Column(updatable = false, nullable = false)
  private UUID id;

  @Column(unique = true, nullable = false, updatable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "is_email_verified", nullable = false)
  private Boolean isEmailVerified;

  /**
   * NATIVE, GOOGLE, KAKAO, NAVER, GITHUB
   * 영속화시 null이라면 NATIVE로 초기화한다.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private ProviderType provider;

  /**
   * ROLE_ADMIN, ROLE_MANAGER, ROLE_USER, ROLE_ANONYMOUS
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleType role;

  @JsonIgnore
  @OneToOne(optional = false, fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  private Profile profile;


  /**
   * 아직 Profile이 할당되지 않은 Account에 대하여, 해당 Profile을 연결한다.
   * @param profile
   */
  public void associateProfile(Profile profile) {

    if(profile.getAccount() != null){
      throw new IllegalStateException(String.format(
        "Profile is already associated with another account. (username: %s)", this.username
      ));
    }
    if(this.profile != null){
      throw new IllegalStateException(String.format(
        "Account is already associated with another profile. (username: %s)", this.username
      ));
    }
    this.profile = profile;
    profile.setAccount(this);
  }

  public UID getUid() {
    return new UID(id);
  }


  /**
   *
   * @param currentPassword
   * @param password
   * @return Result
   * - BAD_CREDENTIALS: currentPassword가 일치하지 않는 경우
   * - PASSWORD_SAME_AS_BEFORE: 새로운 비밀번호가 기존 비밀번호와 동일한 경우
   */
  public Result<NullData> changePassword(String currentPassword, Password password) {
    if(!passwordMatches(currentPassword)){
      return Result.of(UserCode.BAD_CREDENTIALS);
    }
    if(currentPassword.equals(password.getValue())){
      return Result.of(UserCode.PASSWORD_SAME_AS_BEFORE);
    }
    this.password = password.getValue();
    return Result.success();
  }

  /**
   * 언제나 이메일의 인증과 변경은 함께 이루어진다.
   */
  public void verifyAndChangeEmail(String email) {
    this.email = email;
    this.isEmailVerified = true;
  }

  public boolean passwordMatches(String rawPassword) {
    return Password.matches(rawPassword, this.password);
  }
}
