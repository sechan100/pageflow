package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.response.BookRes;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookStatusUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.pageflow.common.utility.Post;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequiredArgsConstructor
public class BookStatusWebAdapter {
  private final BookStatusUseCase bookStatusUseCase;
  private final RequestContext rqcxt;


  @Post("/user/books/{bookId}/status")
  @Operation(summary = "책 상태 변경")
  @SetBookPermission
  public BookRes changeBookStatus(
    @BookId @PathVariable UUID bookId,
    @RequestParam BookStatusCmd cmd
  ) {
    UID uid = rqcxt.getUid();
    BookDto result = switch(cmd) {
      case PUBLISH -> bookStatusUseCase.publish(bookId);
      case CANCEL_REVISE -> bookStatusUseCase.cancelRevise(bookId);
      case MERGE_REVISION -> bookStatusUseCase.mergeRevision(bookId);
      case REVISE -> bookStatusUseCase.revise(bookId);
    };
    return BookRes.from(result);
  }


  public enum BookStatusCmd {
    PUBLISH,
    REVISE,
    CANCEL_REVISE,
    MERGE_REVISION
  }

}
