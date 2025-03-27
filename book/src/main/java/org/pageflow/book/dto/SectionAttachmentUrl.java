package org.pageflow.book.dto;

import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public class SectionAttachmentUrl {
  private final String url;

  public SectionAttachmentUrl(String url) {
    this.url = url;
  }
}
