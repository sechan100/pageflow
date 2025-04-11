package org.pageflow.book.adapter.in.res.book;

import lombok.Value;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.application.dto.book.WithAuthorBookDto;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.domain.enums.BookVisibility;

import java.util.UUID;

/**
 * 작가에게만 반환해야하는 책 응답 객체
 * 다른 사용자들에게 공개되면 안되는 정보들이 포함되어 있다.
 *
 * @author : sechan
 */
@Value
public class AuthorPrivateBookRes {
  UUID id;
  String title;
  String coverImageUrl;
  String description;
  BookStatus status;
  BookVisibility visibility;

  public AuthorPrivateBookRes(WithAuthorBookDto book) {
    this.id = book.getId();
    this.title = book.getTitle();
    this.coverImageUrl = book.getCoverImageUrl();
    this.description = book.getDescription();
    this.status = book.getStatus();
    this.visibility = book.getVisibility();
  }

  public AuthorPrivateBookRes(BookDto dto) {
    this.id = dto.getId();
    this.title = dto.getTitle();
    this.coverImageUrl = dto.getCoverImageUrl();
    this.description = dto.getDescription();
    this.status = dto.getStatus();
    this.visibility = dto.getVisibility();
  }
}
