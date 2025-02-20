package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.user.domain.entity.Profile;

import java.util.UUID;


/**
 * @author : sechan
 */
@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "book")
public class Book extends BaseJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  private Profile author;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String coverImageUrl;



  public Book(
    UUID id,
    Profile author,
    String title,
    String coverImageUrl
  ) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.coverImageUrl = coverImageUrl;
  }


}
