package org.pageflow.book.application.dto.book;

import lombok.Value;
import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.domain.entity.Book;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Value
public class PublishedBookDto {
  UUID id;
  String title;
  String coverImageUrl;
  String description;
  List<PublishedRecordDto> publishedRecords;
  AuthorProfileDto authorProfile;
  TocDto toc;
  int totalCharCount;

  public PublishedBookDto(Book book, AuthorProfileDto authorProfile, TocDto toc, int totalCharCount) {
    this.id = book.getId();
    this.title = book.getTitle();
    this.coverImageUrl = book.getCoverImageUrl();
    this.description = book.getDescription();
    this.publishedRecords = book.getPublishedRecords().stream().map(PublishedRecordDto::new).toList();
    this.authorProfile = authorProfile;
    this.toc = toc;
    this.totalCharCount = totalCharCount;
  }
}
