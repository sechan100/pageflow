package org.pageflow.boundedcontext.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.auth.domain.token.RefreshToken;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.AccountJpaEntity;
import org.pageflow.shared.jpa.JpaEntity;

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
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private AccountJpaEntity account;

  @Enumerated(EnumType.STRING)
  private RoleType role;

  @Embedded
  private RefreshToken refreshToken;



  public Session(
    UUID id,
    AccountJpaEntity account,
    RoleType role,
    RefreshToken refreshToken
  ) {
    this.id = id;
    this.account = account;
    this.role = role;
    this.refreshToken = refreshToken;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > refreshToken.getExp();
  }

}
