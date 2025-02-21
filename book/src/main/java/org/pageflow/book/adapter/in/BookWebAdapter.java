package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.CreateBookReq;
import org.pageflow.book.adapter.in.response.BookRes;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.*;
import org.pageflow.book.port.in.BookQueries;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.pageflow.common.utility.Get;
import org.pageflow.common.utility.Post;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequiredArgsConstructor
public class BookWebAdapter {
  private final BookUseCase bookUseCase;
  private final BookQueries bookQueries;
  private final RequestContext rqcx;

  @Post("/user/books")
  @Operation(summary = "책 생성")
  public BookRes createBook(@RequestBody CreateBookReq req) {
    UID uid = rqcx.getUid();
    BookTitle title = BookTitle.validOf(req.getTitle());
    BookDto result = bookUseCase.createBook(uid, title, null);
    return BookRes.from(result);
  }

  @Get("/user/books")
  @Operation(summary = "내 책장 조회")
  public MyBooks getMyBooks() {
    MyBooks myBooks = bookQueries.queryMyBooks(rqcx.getUid());
    return myBooks;
  }

  @Get("/user/books/{bookId}")
  @Operation(summary = "책 조회")
  public BookDtoWithAuthor getBook(@PathVariable UUID bookId) {
    BookDtoWithAuthor bookWithAuthor = bookQueries.queryBook(bookId);
    return bookWithAuthor;
  }

  @Get("/user/books/{bookId}/folders/{folderId}")
  @Operation(summary = "폴더 조회")
  public FolderDto getFolder(@PathVariable UUID bookId, @PathVariable UUID folderId) {
    FolderDto folder = bookQueries.queryFolder(folderId);
    return folder;
  }

  @Get("/user/books/{bookId}/sections/{sectionId}")
  @Operation(summary = "섹션 조회")
  public SectionDto getSection(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    SectionDto section = bookQueries.querySection(sectionId);
    return section;
  }

  @Get("/user/books/{bookId}/sections/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public SectionDtoWithContent getSectionWithContent(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    SectionDtoWithContent section = bookQueries.querySectionWithContent(sectionId);
    return section;
  }

}
