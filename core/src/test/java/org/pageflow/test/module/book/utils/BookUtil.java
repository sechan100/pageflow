package org.pageflow.test.module.book.utils;

import org.pageflow.book.application.BookPermission;
import org.pageflow.test.e2e.shared.API;
import org.pageflow.test.e2e.shared.TestRes;

import java.util.UUID;

/**
 * @author : sechan
 */
public abstract class BookUtil {
  public static BookPermission fullPermission(UUID bookId) {
    return BookPermission.ofAuthor(bookId);
  }

  /**
   * 테스트용 책 생성
   */
  public static UUID createTestBook(API api, String title) {
    TestRes createRes = api.post("/user/books", String.format("""
        {
          "title": "%s"
        }
      """, title));
    createRes.isSuccess();
    return UUID.fromString(createRes.getData().get("id").asText());
  }
}
