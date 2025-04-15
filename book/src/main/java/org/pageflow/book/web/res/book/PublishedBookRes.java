package org.pageflow.book.web.res.book;

import lombok.Value;
import org.pageflow.book.application.dto.book.PublishedBookDto;
import org.pageflow.book.web.res.author.AuthorProfileRes;
import org.pageflow.book.web.res.node.TocRes;

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
  AuthorProfileRes authorProfile;
  TocRes toc;
  int totalCharCount;

  public static PublishedBookRes from(PublishedBookDto dto) {
    return new PublishedBookRes(
      dto.getId(),
      dto.getTitle(),
      dto.getCoverImageUrl(),
      dto.getDescription(),
      dto.getPublishedRecords().stream()
        .map(PublishedRecordRes::new)
        .toList(),
      new AuthorProfileRes(dto.getAuthorProfile()),
      TocRes.from(dto.getToc()),
      dto.getTotalCharCount()
    );
  }
}
