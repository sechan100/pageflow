package org.pageflow.book.adapter.in.res.book;

import lombok.Value;
import org.pageflow.book.adapter.in.res.AuthorRes;
import org.pageflow.book.application.dto.book.PublishedBookDto;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class PublishedBookRes {
  UUID id;
  String title;
  String coverImageUrl;
  String description;
  List<PublishedRecordRes> publishedRecords;
  AuthorRes author;

  public PublishedBookRes(PublishedBookDto dto) {
    this.id = dto.getId();
    this.title = dto.getTitle();
    this.coverImageUrl = dto.getCoverImageUrl();
    this.description = dto.getDescription();
    this.publishedRecords = dto.getPublishedRecords().stream()
      .map(PublishedRecordRes::new)
      .toList();
    this.author = new AuthorRes(dto.getAuthor());
  }
}
