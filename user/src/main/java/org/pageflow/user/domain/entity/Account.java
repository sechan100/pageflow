package org.pageflow.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.shared.jpa.BaseJpaEntity;
import org.pageflow.common.user.ProviderType;
import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;
import org.pageflow.user.application.config.PasswordEncoderConfig;

import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "account", indexes = {
  @Index(name = "idx_account_username", columnList = "username")
})
public class Account extends BaseJpaEntity {

  @Id
  @Column(updatable = false, nullable = false)
  private UUID id;

  @Column(unique = true, nullable = false, updatable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private boolean emailVerified;

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


  public Account(
    UUID id,
    String username,
    String password,
    String email,
    boolean emailVerified,
    ProviderType provider,
    RoleType role,
    Profile profile
  ) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.emailVerified = emailVerified;
    this.provider = provider;
    this.role = role;
    this.profile = profile;
  }

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

  public void changeEmail(String email) {
    this.email = email;
    this.emailVerified = false;
  }

  public void changePassword(String password) {
    this.password = password;
  }

  public void verifyEmail() {
    this.emailVerified = true;
  }

  public void unverifyEmail() {
    this.emailVerified = false;
  }

  public boolean passwordMatches(String rawPassword) {
    return PasswordEncoderConfig.PASSWORD_ENCODER.matches(rawPassword, this.password);
  }
}
