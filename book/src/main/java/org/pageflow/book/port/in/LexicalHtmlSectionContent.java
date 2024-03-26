package org.pageflow.book.port.in;

import org.pageflow.common.utility.SingleValueWrapper;

/**
 * meta의 Lexical Editor에서 발생한 HTML Content 객체
 *
 * @author : sechan
 */
public class LexicalHtmlSectionContent extends SingleValueWrapper<String> {
  public LexicalHtmlSectionContent(String value) {
    super(value);
  }
}
