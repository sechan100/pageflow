package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.common.jpa.BaseJpaEntity;

import java.time.LocalDateTime;

/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "published_record")
public class PublishedRecord extends BaseJpaEntity {

  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false, updatable = false)
  private Book book;

  /**
   * 책의 인쇄 부 수를 의미한다.
   * 이 도메인에서는 조회수로 사용된다.
   */
  @Getter
  @Column(nullable = false)
  private long printingCount;

  @Getter
  @Column(nullable = false, updatable = false)
  private int edition;


  public PublishedRecord(Book book, int edition) {
    this.id = null;
    this.book = book;
    this.printingCount = 0;
    this.edition = edition;
  }

  public LocalDateTime getPublishedAt() {
    return book.getCreatedDatetime();
  }

  /**
   * 조회수 올리기
   */
  public void incrementPrintingCount() {
    this.printingCount++;
  }

}
