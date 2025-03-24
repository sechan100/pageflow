package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.CreateBookReq;
import org.pageflow.book.application.BookId;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequestMapping("/user/books")
@RequiredArgsConstructor
public class BookWebAdapter {
  private final BookUseCase bookUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "책 생성")
  public Result<BookDto> createBook(@RequestBody CreateBookReq req) {
    UID uid = rqcxt.getUid();
    BookTitle title = BookTitle.of(req.getTitle());
    Result<BookDto> result = bookUseCase.createBook(uid, title, null);
    return result;
  }

  @GetMapping("/{bookId}")
  @Operation(summary = "책 조회")
  @SetBookPermission
  public BookDtoWithAuthor getBook(@PathVariable @BookId UUID bookId) {
    BookDtoWithAuthor bookWithAuthor = bookUseCase.queryBook(bookId);
    return bookWithAuthor;
  }

  @GetMapping("")
  @Operation(summary = "내 책장 조회")
  public MyBooks getMyBooks() {
    MyBooks myBooks = bookUseCase.queryMyBooks(rqcxt.getUid());
    return myBooks;
  }

  @DeleteMapping("/{bookId}")
  @Operation(summary = "책 삭제")
  @SetBookPermission
  public void deleteBook(@PathVariable @BookId UUID bookId) {
    bookUseCase.deleteBook(bookId);
  }

}
