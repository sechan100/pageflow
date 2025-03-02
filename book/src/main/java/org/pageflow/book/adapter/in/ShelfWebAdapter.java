package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.response.BookRes;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookShelfUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.pageflow.common.utility.Delete;
import org.pageflow.common.utility.Post;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


  @Post("")
  @Operation(summary = "책을 책장에 추가")
  @SetBookPermission
  public BookRes addBookToShelf(
    @BookId @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    BookDto result = bookShelfUseCase.addBookToShelf(bookId, uid);
    return BookRes.from(result);
  }

  @Delete("")
  @Operation(summary = "책을 책장에서 제거")
  @SetBookPermission
  public void removeBookFromShelf(
    @BookId @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    bookShelfUseCase.removeBookFromShelf(bookId, uid);
  }

}
