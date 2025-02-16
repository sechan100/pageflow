package org.pageflow.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.shared.jpa.JpaEntity;
import org.pageflow.common.user.RoleType;

import java.util.UUID;


/**
 * @author : sechan
 */
@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "session")
public class Session implements JpaEntity {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
  @JoinColumn(name = "account_id", updatable = false)
  private Account account;

  @Enumerated(EnumType.STRING)
  private RoleType role;

  @Embedded
  private RefreshToken refreshToken;



  public Session(
    UUID id,
    Account account,
    RoleType role,
    RefreshToken refreshToken
  ) {
    this.id = id;
    this.account = account;
    this.role = role;
    this.refreshToken = refreshToken;
  }

  public static Session issue(Account account) {
    return new Session(
      UUID.randomUUID(),
      account,
      account.getRole(),
      RefreshToken.issue()
    );
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > refreshToken.getExp();
  }

}
