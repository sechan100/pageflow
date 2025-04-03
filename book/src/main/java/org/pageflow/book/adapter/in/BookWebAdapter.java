package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.CreateBookReq;
import org.pageflow.book.adapter.in.form.UpdateBookForm;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequestMapping("/user/books")
@RequiredArgsConstructor
public class BookWebAdapter {
  private final BookAccessPermitter bookAccessPermitter;
  private final BookUseCase bookUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "책 생성")
  public Result<BookDto> createBook(@RequestBody CreateBookReq req) {
    UID uid = rqcxt.getUid();
    BookTitle title = BookTitle.create(req.getTitle());
    Result<BookDto> result = bookUseCase.createBook(uid, title, null);
    return result;
  }

  @GetMapping("")
  @Operation(summary = "내 책장 조회")
  public MyBooks getMyBooks() {
    MyBooks myBooks = bookUseCase.queryMyBooks(rqcxt.getUid());
    return myBooks;
  }

  @GetMapping("/{bookId}")
  @Operation(summary = "책 조회")
  public BookDtoWithAuthor getBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    BookDtoWithAuthor bookWithAuthor = bookUseCase.queryBook(bookId);
    return bookWithAuthor;
  }

  @PostMapping("/{bookId}/title")
  @Operation(summary = "책 제목 수정")
  public Result<BookDto> changeBookTitle(
    @PathVariable UUID bookId,
    @RequestBody UpdateBookForm form
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    BookTitle title = BookTitle.create(form.getTitle());
    Result<BookDto> result = bookUseCase.changeBookTitle(bookId, title);
    return result;
  }

  @PostMapping("/{bookId}/cover-image")
  @Operation(summary = "책 표지 이미지 수정")
  public Result<BookDto> changeBookCoverImage(
    @PathVariable UUID bookId,
    @RequestPart(name = "coverImage") MultipartFile coverImage
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    Result<BookDto> result = bookUseCase.changeBookCoverImage(bookId, coverImage);
    return result;
  }

  @DeleteMapping("/{bookId}")
  @Operation(summary = "책 삭제")
  public void deleteBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    bookUseCase.deleteBook(bookId);
  }

}
