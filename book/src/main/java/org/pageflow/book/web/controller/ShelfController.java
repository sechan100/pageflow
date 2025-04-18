package org.pageflow.book.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.usecase.BookShelfUseCase;
import org.pageflow.book.web.res.book.SimpleBookRes;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@RequestMapping("/user/books/{bookId}/shelf")
@RestController
@RequiredArgsConstructor
public class ShelfController {
  private final RequestContext rqcxt;
  private final BookShelfUseCase bookShelfUseCase;


  @PostMapping("")
  @Operation(summary = "책을 책장에 추가")
  public Result<SimpleBookRes> addBookToShelf(
    @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookShelfUseCase.addBookToShelf(uid, bookId);
    if(result.isFailure()) {
      return (Result) result;
    }
    SimpleBookRes res = new SimpleBookRes(result.getSuccessData());
    return Result.ok(res);
  }

  @DeleteMapping("")
  @Operation(summary = "책을 책장에서 제거")
  public Result removeBookFromShelf(
    @PathVariable UUID bookId
  ) {
    UID uid = rqcxt.getUid();
    return bookShelfUseCase.removeBookFromShelf(uid, bookId);
  }

}
