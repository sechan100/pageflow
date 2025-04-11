package org.pageflow.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.common.jpa.JpaEntity;
import org.pageflow.common.user.RoleType;

import java.util.UUID;


/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "session")
public class Session implements JpaEntity {

  @Id
  @Getter
  private UUID id;

  @Getter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", updatable = false, nullable = false)
  private User user;

  @Getter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleType role;

  @Getter
  @Embedded
  private RefreshToken refreshToken;


  public Session(
    UUID id,
    User user,
    RoleType role,
    RefreshToken refreshToken
  ) {
    this.id = id;
    this.user = user;
    this.role = role;
    this.refreshToken = refreshToken;
  }

  public static Session issue(User user) {
    return new Session(
      UUID.randomUUID(),
      user,
      user.getRole(),
      RefreshToken.issue()
    );
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > refreshToken.getExp();
  }

}
