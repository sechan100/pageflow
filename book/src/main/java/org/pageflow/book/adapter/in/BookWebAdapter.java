package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.CreateBookReq;
import org.pageflow.book.adapter.in.response.BookRes;
import org.pageflow.book.application.BookId;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.pageflow.common.utility.Delete;
import org.pageflow.common.utility.Get;
import org.pageflow.common.utility.Post;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


  @Post("")
  @Operation(summary = "책 생성")
  public BookRes createBook(@RequestBody CreateBookReq req) {
    UID uid = rqcxt.getUid();
    BookTitle title = BookTitle.of(req.getTitle());
    BookDto result = bookUseCase.createBook(uid, title, null);
    return BookRes.from(result);
  }

  @Get("/{bookId}")
  @Operation(summary = "책 조회")
  @SetBookPermission
  public BookDtoWithAuthor getBook(@PathVariable @BookId UUID bookId) {
    BookDtoWithAuthor bookWithAuthor = bookUseCase.queryBook(bookId);
    return bookWithAuthor;
  }

  @Get("")
  @Operation(summary = "내 책장 조회")
  public MyBooks getMyBooks() {
    MyBooks myBooks = bookUseCase.queryMyBooks(rqcxt.getUid());
    return myBooks;
  }

  @Delete("/{bookId}")
  @Operation(summary = "책 삭제")
  @SetBookPermission
  public void deleteBook(@PathVariable @BookId UUID bookId) {
    bookUseCase.deleteBook(bookId);
  }

}
