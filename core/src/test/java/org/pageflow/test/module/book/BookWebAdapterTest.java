//package org.pageflow.test.module.book;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.pageflow.book.port.out.jpa.BookPersistencePort;
//import org.pageflow.test.e2e.ApiFactory;
//import org.pageflow.test.e2e.TestRes;
//
//import java.util.UUID;
//
/// **
// * @author : sechan
// */
//@RequiredArgsConstructor
//public class BookWebAdapterTest {
//  private final BookPersistencePort bookPersistencePort;
//  private final ApiTestDataCreator apiTestDataCreator;
//  private final ApiFactory apiFactory;
//
//  @Test
//  @DisplayName("책 CRUD")
//  void bookCrudTest() {
//    apiTestDataCreator.createUser("user1");
//    API userApi = apiFactory.user("user1", "user1");
//
//    // 책 생성
//    TestRes createRes = userApi.post("/user/books", """
//        {
//          "title": "테스트 도서"
//        }
//      """);
//    createRes.isSuccess();
//    UUID bookId = UUID.fromString(createRes.getData().get("id").asText());
//
//    // 책 조회
//    TestRes getBookRes = userApi.get("/user/books/" + bookId);
//    getBookRes.isSuccess();
//    assert getBookRes.getData().get("id").asText().equals(bookId.toString());
//    Assertions.assertEquals("테스트 도서", getBookRes.getData().get("title").asText());
//
//    // 내 책장을 통해서 책 조회
//    TestRes myBooks = userApi.get("/user/books");
//    myBooks.isSuccess();
//    Assertions.assertEquals(bookId.toString(), myBooks.getData().get("books").get(0).get("id").asText());
//
//    // 책 삭제
//    TestRes deleteRes = userApi.delete("/user/books/" + bookId);
//    deleteRes.isSuccess();
//
//    // 책장 재조회하여 삭제 확인
//    TestRes myBooks2 = userApi.get("/user/books");
//    myBooks2.isSuccess();
//    Assertions.assertTrue(myBooks2.getData().get("books").isEmpty());
//  }
//}