package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.res.book.SimpleBookRes;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.enums.BookVisibility;
import org.pageflow.book.port.in.ChangeBookStatusUseCase;
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
public class BookSettingsWebAdapter {
  private final RequestContext rqcxt;
  private final ChangeBookStatusUseCase changeBookStatusUseCase;


  @PostMapping("/user/books/{bookId}/status")
  @Operation(summary = "책 상태 변경")
  public Result<SimpleBookRes> changeBookStatus(
    @PathVariable UUID bookId,
    @RequestParam BookStatusCmd cmd
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = switch(cmd) {
      case PUBLISH -> changeBookStatusUseCase.publish(uid, bookId);
      case START_REVISION -> changeBookStatusUseCase.startRevision(uid, bookId);
      case MERGE_REVISION -> changeBookStatusUseCase.mergeRevision(uid, bookId);
      case CANCEL_REVISION -> changeBookStatusUseCase.cancelRevision(uid, bookId);
    };
    if(result.isFailure()) {
      return (Result) result;
    }
    SimpleBookRes res = new SimpleBookRes(result.getSuccessData());
    return Result.success(res);
  }

  @PostMapping("/user/books/{bookId}/visibility")
  @Operation(summary = "책 공개 범위 변경")
  public Result<SimpleBookRes> changeBookVisibility(
    @PathVariable UUID bookId,
    @RequestParam BookVisibility visibility
  ) {
    UID uid = rqcxt.getUid();
    Result<BookDto> result = changeBookStatusUseCase.changeVisibility(uid, bookId, visibility);
    if(result.isFailure()) {
      return (Result) result;
    }
    SimpleBookRes res = new SimpleBookRes(result.getSuccessData());
    return Result.success(res);
  }

}
