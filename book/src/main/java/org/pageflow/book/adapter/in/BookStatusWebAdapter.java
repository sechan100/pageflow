package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.BookStatusUseCase;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  private final BookAccessPermitter bookAccessPermitter;
  private final RequestContext rqcxt;
  private final BookStatusUseCase bookStatusUseCase;


  @PostMapping("/user/books/{bookId}/status")
  @Operation(summary = "책 상태 변경")
  public BookDto changeBookStatus(
    @PathVariable UUID bookId,
    @RequestParam BookStatusCmd cmd
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    BookDto result = switch(cmd) {
      case PUBLISH -> bookStatusUseCase.publish(bookId);
      case CANCEL_REVISE -> bookStatusUseCase.cancelRevise(bookId);
      case MERGE_REVISION -> bookStatusUseCase.revise(bookId);
      case REVISE -> bookStatusUseCase.startRevise(bookId);
    };
    return result;
  }


  public enum BookStatusCmd {
    PUBLISH,
    REVISE,
    CANCEL_REVISE,
    MERGE_REVISION
  }

}
