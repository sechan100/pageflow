package org.pageflow.book.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.pageflow.book.application.BookCode;
import org.pageflow.common.result.Result;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public class SectionHtmlContent {
  private static final String[] BASE_ATTRIBUTES = {"class", "style", "dir"};
  private static final Safelist SAFELIST = new Safelist()
    .addTags(
      // 노드 관련 태그
      "div", "br", "span", "p", "a", "blockquote", "code", "pre",
      "h1", "h2", "h3", "h4", "h5", "h6", "hr", "img", "li", "ol", "ul",
      // 형식 관련 태그
      "b", "strong", "u", "i", "s", "em")

    // 속성 추가
    .addAttributes("div", BASE_ATTRIBUTES)
    .addAttributes("h1", BASE_ATTRIBUTES)
    .addAttributes("h2", BASE_ATTRIBUTES)
    .addAttributes("h3", BASE_ATTRIBUTES)
    .addAttributes("h4", BASE_ATTRIBUTES)
    .addAttributes("h5", BASE_ATTRIBUTES)
    .addAttributes("h6", BASE_ATTRIBUTES)
    .addAttributes("p", BASE_ATTRIBUTES)
    .addAttributes("br", BASE_ATTRIBUTES)
    .addAttributes("span", ArrayMerge.merge(BASE_ATTRIBUTES, "data-tab"))
    .addAttributes("a", ArrayMerge.merge(BASE_ATTRIBUTES, "href", "title", "target", "rel"))
    .addAttributes("blockquote", ArrayMerge.merge(BASE_ATTRIBUTES, "cite"))
    .addAttributes("code", BASE_ATTRIBUTES)
    .addAttributes("pre", ArrayMerge.merge(BASE_ATTRIBUTES, "spellcheck", "data-language", "data-highlight-language"))
    .addAttributes("hr", BASE_ATTRIBUTES)
    .addAttributes("img", ArrayMerge.merge(BASE_ATTRIBUTES, "alt", "height", "src", "title", "width", "data-position", "data-show-caption"))
    .addAttributes("li", ArrayMerge.merge(BASE_ATTRIBUTES, "value"))
    .addAttributes("ol", ArrayMerge.merge(BASE_ATTRIBUTES, "start", "type"))
    .addAttributes("ul", ArrayMerge.merge(BASE_ATTRIBUTES, "type"))
    .addAttributes("b", BASE_ATTRIBUTES)
    .addAttributes("strong", BASE_ATTRIBUTES)
    .addAttributes("u", BASE_ATTRIBUTES)
    .addAttributes("i", BASE_ATTRIBUTES)
    .addAttributes("s", BASE_ATTRIBUTES)
    .addAttributes("em", BASE_ATTRIBUTES)

    // 프로토콜 추가
    .addProtocols("a", "href", "ftp", "http", "https", "mailto")
    .addProtocols("blockquote", "cite", "http", "https")
    .addProtocols("img", "src", "http", "https");

  private final String content;
  // clean된 html과 원본 html이 같은지 여부
  private final boolean isSanitizationConsistent;

  /**
   * @code SECTION_HTML_CONTENT_PARSE_ERROR: html 파싱에 실패한 경우
   */
  public static Result<SectionHtmlContent> of(String html) {
    try {
      Document document = Jsoup.parse(html);
      Document.OutputSettings outputSettings = new Document.OutputSettings();
      outputSettings.prettyPrint(false);
      String cleanHtml = Jsoup.clean(html, "", SAFELIST, outputSettings);
      boolean isSanitizationConsistent = cleanHtml.equals(html);
      SectionHtmlContent content = new SectionHtmlContent(cleanHtml, isSanitizationConsistent);
      return Result.success(content);
    } catch(Exception e) {
      return Result.of(BookCode.SECTION_HTML_CONTENT_PARSE_ERROR, e);
    }
  }


}


abstract class ArrayMerge {
  public static String[] merge(String[] array1, String... array2) {
    String[] result = new String[array1.length + array2.length];

    System.arraycopy(array1, 0, result, 0, array1.length);
    System.arraycopy(array2, 0, result, array1.length, array2.length);

    return result;
  }
}