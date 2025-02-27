package org.pageflow.book.port.in;

import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.token.BookContext;

/**
 * @author : sechan
 */
public interface BookStatusUseCase {

  /**
   * 책을 출판한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#DRAFT}
   * 또는 {@link org.pageflow.book.domain.enums.BookStatus#REVISING}에서 사용 가능하다.
   *
   * edition을 1 증가시킨다.
   * @param ctx
   * @return
   */
  BookDto publish(BookContext ctx);

  /**
   * 책을 개정상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#PUBLISHED}일 때 사용 가능하며,
   * 기존의 출판상태인 책은 독자들에게 여전히 유효하다.
   * @param ctx
   * @return
   */
  BookDto revise(BookContext ctx);

  /**
   * 개정을 취소하고 출판상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#REVISING}일 때 사용 가능하다.
   * @param ctx
   * @return
   */
  BookDto cancelRevise(BookContext ctx);

  /**
   * 개정을 병합하여 출판상태로 변경한다.
   * {@link org.pageflow.book.domain.enums.BookStatus#REVISING}일 때 사용 가능하다.
   * edition을 올리지 않는다.
   *
   * @apiNote 해당 함수는 edition을 증가시키지 않음으로 간단한 오탈자 수정등의 변경에 용의하다.
   * 책의 내용에 주요한 변경이 있는 경우 사용자는 {@link #publish}를 이용하여 책을 재출판 및 개정하는 것이 좋다.
   * @param ctx
   * @return
   */
  BookDto mergeRevision(BookContext ctx);
}
