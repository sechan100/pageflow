package org.pageflow.test.module.book;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.test.e2e.API;
import org.pageflow.test.e2e.ApiFactory;
import org.pageflow.test.e2e.PageflowIntegrationTest;
import org.pageflow.test.e2e.TestRes;
import org.pageflow.test.shared.DataCreator;

import java.util.UUID;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
@RequiredArgsConstructor
public class BookCrudTest {
  private final DataCreator dataCreator;
  private final ApiFactory apiFactory;


  @Test
  @DisplayName("책 CRUD")
  void bookCrudTest(){
    dataCreator.createUser("user1");
    API userApi = apiFactory.user("user1", "user1");

    // 책 생성
    TestRes res = userApi.post("/user/books", """
        {
          "title": "test book 1"
        }
      """);
    res.isSuccess();
    UUID bookId = UUID.fromString(res.getData().get("id").asText());

    // 책 조회
    TestRes getBookRes = userApi.get("/user/books/" + bookId);
    getBookRes.isSuccess();
    assert getBookRes.getData().get("id").asText().equals(bookId.toString());

    // 내 책장을 통해서 책 조회
    TestRes myBooks = userApi.get("/user/books");
    myBooks.isSuccess();
    assert myBooks.getData().get("books").get(0).get("id").asText().equals(bookId.toString());

    // 책 삭제
    TestRes deleteRes = userApi.delete("/user/books/" + bookId);
    deleteRes.isSuccess();
    TestRes myBooks2 = userApi.get("/user/books");
    myBooks.isSuccess();
    assert myBooks2.getData().get("books").isEmpty();
  }
}
