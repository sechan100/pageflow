package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.BookDto;
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
  private final BookShelfUseCase bookShelfUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "책을 책장에 추가")
  @SetBookPermission
  public BookDto addBookToShelf(
    @BookId @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    BookDto result = bookShelfUseCase.addBookToShelf(bookId, uid);
    return result;
  }

  @DeleteMapping("")
  @Operation(summary = "책을 책장에서 제거")
  @SetBookPermission
  public void removeBookFromShelf(
    @BookId @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    bookShelfUseCase.removeBookFromShelf(bookId, uid);
  }

}
