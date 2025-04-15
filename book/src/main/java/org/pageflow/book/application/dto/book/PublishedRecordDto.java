package org.pageflow.book.application.dto.book;

import lombok.Value;
import org.pageflow.book.domain.book.entity.PublishedRecord;

import java.time.LocalDateTime;

/**
 * @author : sechan
 */
@Value
public class PublishedRecordDto {
  long printingCount;
  int edition;
  LocalDateTime publishedAt;

  public PublishedRecordDto(PublishedRecord publishedRecord) {
    this.printingCount = publishedRecord.getPrintingCount();
    this.edition = publishedRecord.getEdition();
    this.publishedAt = publishedRecord.getPublishedAt();
  }
}
