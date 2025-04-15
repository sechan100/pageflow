package org.pageflow.book.web.res.book;

import lombok.Value;
import org.pageflow.book.application.dto.book.PublishedRecordDto;

import java.time.LocalDateTime;

/**
 * @author : sechan
 */
@Value
public class PublishedRecordRes {
  long printingCount;
  int edition;
  LocalDateTime publishedAt;

  public PublishedRecordRes(PublishedRecordDto dto) {
    this.printingCount = dto.getPrintingCount();
    this.edition = dto.getEdition();
    this.publishedAt = dto.getPublishedAt();
  }
}
