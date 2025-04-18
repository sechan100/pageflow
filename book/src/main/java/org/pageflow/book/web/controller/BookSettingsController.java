package org.pageflow.book.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.book.entity.BookVisibility;
import org.pageflow.book.usecase.ChangeBookStatusUseCase;
import org.pageflow.book.web.form.BookStatusCmd;
import org.pageflow.book.web.res.book.SimpleBookRes;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class BookSettingsController {
  private final RequestContext rqcxt;
  private final ChangeBookStatusUseCase changeBookStatusUseCase;


  @PostMapping("/user/books/{bookId}/status")
  @Operation(summary = "책 상태 변경")
  public SimpleBookRes changeBookStatus(
    @PathVariable UUID bookId,
    @RequestParam BookStatusCmd cmd
  ) {
    UID uid = rqcxt.getUid();
    BookDto bookDto = switch(cmd) {
      case PUBLISH -> changeBookStatusUseCase.publish(uid, bookId);
      case START_REVISION -> changeBookStatusUseCase.startRevision(uid, bookId);
      case MERGE_REVISION -> changeBookStatusUseCase.mergeRevision(uid, bookId);
      case CANCEL_REVISION -> changeBookStatusUseCase.cancelRevision(uid, bookId);
    };
    return new SimpleBookRes(bookDto);
  }

  @PostMapping("/user/books/{bookId}/visibility")
  @Operation(summary = "책 공개 범위 변경")
  public SimpleBookRes changeBookVisibility(
    @PathVariable UUID bookId,
    @RequestParam BookVisibility visibility
  ) {
    UID uid = rqcxt.getUid();
    BookDto bookDto = changeBookStatusUseCase.changeVisibility(uid, bookId, visibility);
    return new SimpleBookRes(bookDto);
  }

}
