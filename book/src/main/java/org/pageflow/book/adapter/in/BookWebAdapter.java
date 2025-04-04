package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.BookForm;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.BookDtoWithAuthor;
import org.pageflow.book.application.dto.MyBooks;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequestMapping("/user/books")
@RequiredArgsConstructor
public class BookWebAdapter {
  private final BookUseCase bookUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "책 생성")
  public Result<BookDto> createBook(@Valid @RequestBody BookForm.Create form) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookUseCase.createBook(uid, form.getTitle(), null);
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
  public Result<BookDtoWithAuthor> getBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    Result<BookDtoWithAuthor> result = bookUseCase.readBook(uid, bookId);
    return result;
  }

  @PostMapping("/{bookId}/title")
  @Operation(summary = "책 제목 수정")
  public Result<BookDto> changeBookTitle(
    @PathVariable UUID bookId,
    @Valid @RequestBody BookForm.Update form
  ) {
    UID uid = rqcxt.getUid();
    BookTitle title = BookTitle.create(form.getTitle());
    Result<BookDto> result = bookUseCase.changeBookTitle(uid, bookId, title);
    return result;
  }

  @PostMapping("/{bookId}/cover-image")
  @Operation(summary = "책 표지 이미지 수정")
  public Result<BookDto> changeBookCoverImage(
    @PathVariable UUID bookId,
    @RequestPart(name = "coverImage") MultipartFile coverImage
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookUseCase.changeBookCoverImage(uid, bookId, coverImage);
    return result;
  }

  @DeleteMapping("/{bookId}")
  @Operation(summary = "책 삭제")
  public Result deleteBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    Result result = bookUseCase.deleteBook(uid, bookId);
    return result;
  }

}
