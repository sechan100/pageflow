package org.pageflow.test.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.BookPermission;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.test.book.shared.BookCreator;
import org.pageflow.test.shared.PageflowIntegrationTest;
import org.pageflow.test.user.shared.LoginExcutor;
import org.pageflow.test.user.shared.SignupExcetuor;
import org.pageflow.user.adapter.in.res.UserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@PageflowIntegrationTest
public class BookTest {
  @Autowired
  private BookCreator bookCreator;
  @Autowired
  private BookUseCase bookUseCase;
  @Autowired
  private BookAccessPermitter bookAccessPermitter;
  @Autowired
  private LoginExcutor loginExcutor;
  @Autowired
  private SignupExcetuor signupExcetuor;


  @Test
  @DisplayName("책 생성")
  void create(){
    UserRes user = signupExcetuor.signup();
    BookDto book = bookCreator.createBook(user.getUid(), "테스트 책");
    assert "테스트 책".equals(book.getTitle());
  }

  @Test
  @Transactional
  @DisplayName("책 필드 변경")
  void changeBookProperties(){
    UserRes user = signupExcetuor.signup();
    BookDto book = bookCreator.createBook(user.getUid(), "테스트 책");
    UUID bookId = book.getId();

    BookPermission permission = bookAccessPermitter.getAuthorPermission(bookId, user.getUid());
    // 제목 변경
    BookDto titleChangedBook = bookUseCase.changeBookTitle(permission, BookTitle.of("테스트 책 2"));
    assert "테스트 책 2".equals(titleChangedBook.getTitle());
  }
}
