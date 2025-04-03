package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.BookShelfUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RequestMapping("/user/books/{bookId}/shelf")
@RestController
@RequiredArgsConstructor
public class ShelfWebAdapter {
  private final BookAccessPermitter bookAccessPermitter;
  private final RequestContext rqcxt;
  private final BookShelfUseCase bookShelfUseCase;


  @PostMapping("")
  @Operation(summary = "책을 책장에 추가")
  public BookDto addBookToShelf(
    @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    BookDto result = bookShelfUseCase.addBookToShelf(bookId, uid);
    return result;
  }

  @DeleteMapping("")
  @Operation(summary = "책을 책장에서 제거")
  public void removeBookFromShelf(
    @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    bookShelfUseCase.removeBookFromShelf(bookId, uid);
  }

}
