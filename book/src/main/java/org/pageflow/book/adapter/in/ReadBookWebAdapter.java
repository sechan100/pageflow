package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.res.book.PublishedBookRes;
import org.pageflow.book.application.dto.book.PublishedBookDto;
import org.pageflow.book.port.in.ReadBookUseCase;
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
@RequestMapping("/reader/books")
@RequiredArgsConstructor
public class ReadBookWebAdapter {
  private final ReadBookUseCase readBookUseCase;
  private final RequestContext rqcxt;

  @GetMapping("/{bookId}")
  @Operation(summary = "책 조회")
  public Result<PublishedBookRes> getBook(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    Result<PublishedBookDto> result = readBookUseCase.readBook(uid, bookId);
    if(result.isFailure()) {
      return (Result) result;
    }
    PublishedBookRes res = new PublishedBookRes(result.getSuccessData());
    return Result.success(res);
  }


}
