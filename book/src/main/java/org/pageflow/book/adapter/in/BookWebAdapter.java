package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.CreateBookReq;
import org.pageflow.book.adapter.in.response.BookRes;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
import org.pageflow.book.port.in.BookPermission;
import org.pageflow.book.port.in.BookResourcePermitter;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
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
  private final BookResourcePermitter permitter;
  private final RequestContext rqcx;

  @Post("")
  @Operation(summary = "책 생성")
  public BookRes createBook(@RequestBody CreateBookReq req) {
    UID uid = rqcx.getUid();
    BookTitle title = BookTitle.of(req.getTitle());
    BookDto result = bookUseCase.createBook(uid, title, null);
    return BookRes.from(result);
  }

  @Get("/{bookId}")
  @Operation(summary = "책 조회")
  public BookDtoWithAuthor getBook(@PathVariable UUID bookId) {
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    BookDtoWithAuthor bookWithAuthor = bookUseCase.queryBook(permission);
    return bookWithAuthor;
  }

  @Get("")
  @Operation(summary = "내 책장 조회")
  public MyBooks getMyBooks() {
    MyBooks myBooks = bookUseCase.queryMyBooks(rqcx.getUid());
    return myBooks;
  }

  @Post("/{bookId}/delete")
  @Operation(summary = "책 삭제")
  public void deleteBook(@PathVariable UUID bookId) {
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    bookUseCase.deleteBook(permission);
  }

}
