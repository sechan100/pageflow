package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaEntity;
import org.pageflow.shared.jpa.BaseJpaEntity;

import java.util.UUID;


/**
 * @author : sechan
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "book")
public class Book extends BaseJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Setter(AccessLevel.NONE)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "profile_id")
  @Setter(AccessLevel.NONE)
  private ProfileJpaEntity author;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String coverImageUrl;



  public Book(
    UUID id,
    ProfileJpaEntity author,
    String title,
    String coverImageUrl
  ) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.coverImageUrl = coverImageUrl;
  }


}
