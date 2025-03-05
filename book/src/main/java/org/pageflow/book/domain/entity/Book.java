package org.pageflow.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.BookStatusData;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.Result;
import org.pageflow.user.domain.entity.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author : sechan
 */
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "book")
public class Book extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  private Profile author;

  @OneToMany(
    mappedBy = "book",
    fetch = FetchType.LAZY,
    cascade = CascadeType.REMOVE
  )
  private final List<Review> reviews = new ArrayList<>(5);

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "book")
  private final List<TocNode> nodes = new ArrayList<>(20);

  @Getter
  @Column(nullable = false)
  private String title;

  @Getter
  @Column(nullable = false)
  private String coverImageUrl;

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BookStatus status;

  /**
   * publish될 때마다 1씩 증가한다.
   */
  @Getter
  @Column(nullable = false)
  private Integer edition;

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BookVisibility visibility;


  /**
   * DRAFT 상태로 생성하고, PRIVATE하게 설정함
   *
   * @param id
   * @param author
   * @param title
   * @param coverImageUrl
   * @return
   */
  public static Book create(
    UUID id,
    Author author,
    BookTitle title,
    String coverImageUrl
  ) {
    return new Book(
      id,
      author.getProfileJpaEntity(),
      title.getValue(),
      coverImageUrl,
      BookStatus.DRAFT,
      0,
      BookVisibility.PRIVATE
    );
  }


  public Author getAuthor() {
    return new Author(author);
  }

  public void changeTitle(BookTitle title) {
    this.title = title.getValue();
  }

  public void changeCoverImageUrl(String url) {
    this.coverImageUrl = url;
  }

  /**
   * 책의 visibility를 변경한다.
   * {@link BookStatus#DRAFT}인 경우 변경 할 수 없고, PRIVATE로 강제된다.
   *
   * @param visibility
   */
  public void changeVisibility(BookVisibility visibility) {
    if(this.status == BookStatus.DRAFT) {
      throw _getInvalidBookStatusException("DRAFT 상태의 책은 공개범위를 변경할 수 없습니다.");
    }

    this.visibility = visibility;
  }

  /**
   * 책을 출판하고 edition을 1 증가시킨다.
   */
  public void publish() {
    if(this.status == BookStatus.PUBLISHED) {
      throw _getInvalidBookStatusException("이미 발행된 책입니다.");
    }

    this.status = BookStatus.PUBLISHED;
    this.edition++;
  }

  /**
   * 책을 개정상태로 변경한다.
   */
  public void revise() {
    if(this.status == BookStatus.PUBLISHED) {
      this.status = BookStatus.REVISING;
    } else {
      throw _getInvalidBookStatusException("출판된 책만 개정을 시작할 수 있습니다.");
    }
  }

  /**
   * 개정을 취소하고 출판상태로 변경한다.
   */
  public void cancelRevise() {
    if(this.status == BookStatus.REVISING) {
      this.status = BookStatus.PUBLISHED;
    } else {
      throw this._getInvalidBookStatusException("개정 중인 책만 개정을 취소할 수 있습니다.");
    }
  }

  /**
   * 개정을 병합하여 출판상태로 변경한다.
   * edition을 올리지 않는다.
   */
  public void mergeRevision() {
    if(this.status == BookStatus.REVISING) {
      this.status = BookStatus.PUBLISHED;
    } else {
      throw this._getInvalidBookStatusException("개정을 병합하려면 개정 중인 책이어야 합니다.");
    }
  }

  private ProcessResultException _getInvalidBookStatusException(String message) {
    return new ProcessResultException(Result.of(
      BookCode.INVALID_BOOK_STATUS,
      BookStatusData.of(this.status, message)
    ));
  }

}
