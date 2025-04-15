package org.pageflow.book.domain.book.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pageflow.book.domain.book.Author;
import org.pageflow.book.domain.book.BookDescriptionHtmlContent;
import org.pageflow.book.domain.book.BookTitle;
import org.pageflow.common.jpa.BaseJpaEntity;
import org.pageflow.common.property.PropsAware;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * @author : sechan
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "book")
public class Book extends BaseJpaEntity {

  @Id
  @Getter
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id", nullable = false, updatable = false)
  private User author;

  @Getter
  @Column(nullable = false)
  private String title;

  @Getter
  @Column(nullable = false)
  private String coverImageUrl;

  @Getter
  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BookStatus status;

  @Getter
  @OrderBy("createdDatetime ASC")
  @OneToMany(
    fetch = FetchType.LAZY,
    mappedBy = "book",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<PublishedRecord> publishedRecords = new ArrayList<>();

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BookVisibility visibility;


  public Book(
    Author author,
    BookTitle title
  ) {
    this.id = UUID.randomUUID();
    this.author = author.getUserEntity();
    this.title = title.getValue();
    this.coverImageUrl = PropsAware.use().book.defaultCoverImageUrl;
    this.description = "";
    this.status = BookStatus.DRAFT;
    this.visibility = BookVisibility.PERSONAL;
  }

  public Author getAuthor() {
    return new Author(author);
  }

  public boolean isAuthor(UID uid) {
    return this.author.getUid().equals(uid);
  }

  public void changeTitle(BookTitle title) {
    this.title = title.getValue();
  }

  public void changeCoverImageUrl(String url) {
    this.coverImageUrl = url;
  }

  public void changeDescription(BookDescriptionHtmlContent description) {
    this.description = description.getContent();
  }

  public Optional<PublishedRecord> getLatestPublishedRecord() {
    if(publishedRecords.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(publishedRecords.get(publishedRecords.size() - 1));
  }

  public void setStatus(BookStatus status) {
    this.status = status;
  }

  public void setVisibility(BookVisibility visibility) {
    this.visibility = visibility;
  }
}

