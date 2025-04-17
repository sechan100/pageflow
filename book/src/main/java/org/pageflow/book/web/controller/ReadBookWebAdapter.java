package org.pageflow.book.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.book.PublishedBookDto;
import org.pageflow.book.application.dto.node.SectionContentDto;
import org.pageflow.book.usecase.ReadBookUseCase;
import org.pageflow.book.web.res.book.PublishedBookRes;
import org.pageflow.book.web.res.node.SectionContentRes;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequestMapping("/reader/books/{bookId}")
@RequiredArgsConstructor
public class ReadBookWebAdapter {
  private final ReadBookUseCase readBookUseCase;
  private final RequestContext rqcxt;

  @GetMapping("")
  @Operation(summary = "책 조회")
  public Result<PublishedBookRes> getBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    Result<PublishedBookDto> result = readBookUseCase.readBook(uid, bookId);
    if(result.isFailure()) {
      return (Result) result;
    }
    PublishedBookRes res = PublishedBookRes.from(result.get());
    return Result.ok(res);
  }

  @GetMapping("/sections/{sectionId}")
  @Operation(summary = "책 내용 읽기")
  public Result<SectionContentRes> getSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    Result<SectionContentDto> result = readBookUseCase.readSectionContent(uid, bookId, sectionId);
    if(result.isFailure()) {
      return (Result) result;
    }
    SectionContentRes res = SectionContentRes.from(result.get());
    return Result.ok(res);
  }


}
