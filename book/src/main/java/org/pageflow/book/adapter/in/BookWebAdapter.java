package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.BookForm;
import org.pageflow.book.adapter.in.res.book.AuthorPrivateBookRes;
import org.pageflow.book.adapter.in.res.book.MyBookListRes;
import org.pageflow.book.adapter.in.res.book.MyBookRes;
import org.pageflow.book.adapter.in.res.book.SimpleBookRes;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.application.dto.book.MyBooksDto;
import org.pageflow.book.application.dto.book.WithAuthorBookDto;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
  public Result<SimpleBookRes> createBook(@Valid @RequestBody BookForm.Create form) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookUseCase.createBook(uid, form.getTitle(), null);
    if(result.isFailure()) {
      return (Result) result;
    }
    SimpleBookRes res = new SimpleBookRes(result.getSuccessData());
    return Result.success(res);
  }

  @GetMapping("")
  @Operation(summary = "내 책장 조회")
  public Result<MyBookListRes> getMyBookList() {
    MyBooksDto myBooks = bookUseCase.queryMyBooks(rqcxt.getUid());
    List<MyBookRes> bookList = myBooks.getBooks().stream()
      .map(MyBookRes::new)
      .toList();
    MyBookListRes res = new MyBookListRes(bookList);
    return Result.success(res);
  }

  @GetMapping("/{bookId}")
  @Operation(summary = "책 조회")
  public Result<AuthorPrivateBookRes> getBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    Result<WithAuthorBookDto> result = bookUseCase.getBook(uid, bookId);
    if(result.isFailure()) {
      return (Result) result;
    }
    AuthorPrivateBookRes res = new AuthorPrivateBookRes(result.getSuccessData());
    return Result.success(res);
  }

  @PostMapping("/{bookId}/title")
  @Operation(summary = "책 제목 수정")
  public Result<SimpleBookRes> changeBookTitle(
    @PathVariable UUID bookId,
    @Valid @RequestBody BookForm.Title form
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookUseCase.changeBookTitle(uid, bookId, form.getTitle());
    if(result.isFailure()) {
      return (Result) result;
    }
    SimpleBookRes res = new SimpleBookRes(result.getSuccessData());
    return Result.success(res);
  }

  @PostMapping("/{bookId}/cover-image")
  @Operation(summary = "책 표지 이미지 수정")
  public Result<SimpleBookRes> changeBookCoverImage(
    @PathVariable UUID bookId,
    @RequestPart(name = "coverImage") MultipartFile coverImage
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookUseCase.changeBookCoverImage(uid, bookId, coverImage);
    if(result.isFailure()) {
      return (Result) result;
    }
    SimpleBookRes res = new SimpleBookRes(result.getSuccessData());
    return Result.success(res);
  }

  @PostMapping("/{bookId}/description")
  @Operation(summary = "책 설명 수정")
  public Result<SimpleBookRes> changeBookDescription(
    @PathVariable UUID bookId,
    @Valid @RequestBody BookForm.Description form
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookUseCase.changeBookDescription(uid, bookId, form.getDescription());
    if(result.isFailure()) {
      return (Result) result;
    }
    SimpleBookRes res = new SimpleBookRes(result.getSuccessData());
    return Result.success(res);
  }

  @DeleteMapping("/{bookId}")
  @Operation(summary = "책 삭제")
  public Result deleteBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    Result result = bookUseCase.deleteBook(uid, bookId);
    return result;
  }

}
