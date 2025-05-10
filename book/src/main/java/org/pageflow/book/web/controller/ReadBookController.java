package org.pageflow.book.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.book.BookmarkDto;
import org.pageflow.book.application.dto.book.PublishedBookDto;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.usecase.BookmarkUseCase;
import org.pageflow.book.usecase.ReadBookUseCase;
import org.pageflow.book.web.res.book.BookmarkRes;
import org.pageflow.book.web.res.book.PublishedBookRes;
import org.pageflow.book.web.res.node.FolderRes;
import org.pageflow.book.web.res.node.WithContentSectionRes;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequestMapping("/reader/books/{bookId}")
@RequiredArgsConstructor
public class ReadBookController {
  private final ReadBookUseCase readBookUseCase;
  private final BookmarkUseCase bookmarkUseCase;
  private final RequestContext rqcxt;

  @GetMapping("")
  @Operation(summary = "책 조회")
  public PublishedBookRes getBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    PublishedBookDto publishedBookDto = readBookUseCase.readBook(uid, bookId);
    return PublishedBookRes.from(publishedBookDto);
  }

  @GetMapping("/folders/{folderId}")
  @Operation(summary = "폴더 읽기")
  public FolderRes getFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    UID uid = rqcxt.getUid();
    FolderDto folderDto = readBookUseCase.readFolder(uid, bookId, folderId);
    return FolderRes.from(folderDto);
  }

  @GetMapping("/sections/{sectionId}")
  @Operation(summary = "섹션 읽기")
  public WithContentSectionRes getSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    WithContentSectionDto withContentSectionDto = readBookUseCase.readSectionContent(uid, bookId, sectionId);
    return WithContentSectionRes.from(withContentSectionDto);
  }

  @GetMapping("/bookmark")
  @Operation(summary = "책갈피 데이터 가져오기")
  public BookmarkRes getBookmark(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    BookmarkDto bookmarkOrNull = bookmarkUseCase.getBookmarkOrNull(uid, bookId);
    if(bookmarkOrNull == null) {
      return new BookmarkRes(false, null);
    }
    return new BookmarkRes(true, bookmarkOrNull);
  }

  @PostMapping("/bookmark")
  @Operation(summary = "책갈피 저장하기")
  public BookmarkRes saveBookmark(
    @PathVariable UUID bookId,
    @RequestBody BookmarkDto bookmarkDto
  ) {
    UID uid = rqcxt.getUid();
    bookmarkUseCase.markReadingPoint(uid, bookId, bookmarkDto.getTocNodeId(), bookmarkDto.getSectionContentElementId());
    return new BookmarkRes(true, bookmarkDto);
  }


}
