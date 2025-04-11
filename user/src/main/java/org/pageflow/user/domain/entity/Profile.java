package org.pageflow.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.common.user.UID;

import java.util.UUID;


@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "profile")
public class Profile extends BaseJpaEntity {

  // @MapsId: Account의 PK를 Profile의 PK로 사용
  @Id
  private UUID id;

  @Column(nullable = false)
  private String penname;

  @Column(nullable = false, columnDefinition = "VARCHAR(255)")
  private String profileImageUrl;

  @JsonIgnore
  @MapsId
  @JoinColumn(name = "id")
  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PACKAGE)
  @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Account account;

  @Getter
  @Column(nullable = false, columnDefinition = "TEXT")
  private String bio;


  public Profile(
    UUID id,
    String penname,
    String profileImageUrl,
    Account account
  ) {
    this.id = id;
    this.penname = penname;
    this.profileImageUrl = profileImageUrl;
    this.account = account;
  }

  public UID getUid() {
    return new UID(id);
  }

  public void changePenname(String penname) {
    this.penname = penname;
  }

  public void changeProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

}
