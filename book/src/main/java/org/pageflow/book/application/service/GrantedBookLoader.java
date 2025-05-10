package org.pageflow.book.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.persistence.BookRepository;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.ResultException;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrantedBookLoader {
  private final BookRepository bookRepository;

  public Book loadBookWithGrant(UID uid, UUID bookId, BookAccess access) {
    Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResultException(CommonCode.DATA_NOT_FOUND, "책을 찾을 수 없습니다."));
    Result<Void> grant = new BookAccessGranter(uid, book).grant(access);
    if(grant.isFailure()) {
      throw new ResultException(grant);
    }

    return book;
  }
}
