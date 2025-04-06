package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.book.port.in.BookStatusUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
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
public class BookStatusWebAdapter {
  private final RequestContext rqcxt;
  private final BookStatusUseCase bookStatusUseCase;


  @PostMapping("/user/books/{bookId}/status")
  @Operation(summary = "책 상태 변경")
  public Result<BookDto> changeBookStatus(
    @PathVariable UUID bookId,
    @RequestParam BookStatusCmd cmd
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = switch(cmd) {
      case PUBLISH -> bookStatusUseCase.publish(uid, bookId);
      case START_REVISION -> bookStatusUseCase.startRevision(uid, bookId);
      case MERGE_REVISION -> bookStatusUseCase.mergeRevision(uid, bookId);
      case CANCEL_REVISION -> bookStatusUseCase.cancelRevision(uid, bookId);
    };
    return result;
  }

  @PostMapping("/user/books/{bookId}/visibility")
  @Operation(summary = "책 공개 범위 변경")
  public Result<BookDto> changeBookVisibility(
    @PathVariable UUID bookId,
    @RequestParam BookVisibility visibility
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = bookStatusUseCase.changeVisibility(uid, bookId, visibility);
    return result;
  }

}
