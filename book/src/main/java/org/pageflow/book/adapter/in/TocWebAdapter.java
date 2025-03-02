package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.NodeReplaceReq;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.TocNodeUseCase;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.ReplaceNodeCmd;
import org.pageflow.common.utility.Get;
import org.pageflow.common.utility.Post;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequestMapping("/user/books/{bookId}/toc")
@RequiredArgsConstructor
public class TocWebAdapter {
  private final TocUseCase tocUsecase;
  private final TocNodeUseCase tocNodeUseCase;


  @Get("")
  @Operation(summary = "책 목차 조회")
  @SetBookPermission
  public TocDto.Toc getToc(@PathVariable @BookId UUID bookId) {
    TocDto.Toc toc = tocUsecase.loadToc(bookId);
    return toc;
  }

  @Post("/replace-node")
  @Operation(summary = "목차 노드 재배치")
  @SetBookPermission
  public void reorder(
    @PathVariable @BookId UUID bookId,
    @RequestBody NodeReplaceReq req
  ){
    ReplaceNodeCmd cmd = new ReplaceNodeCmd(
      bookId,
      req.getTargetNodeId(),
      req.getDestFolderId(),
      req.getDestIndex()
    );
    tocNodeUseCase.replaceNode(bookId, cmd);
  }

}
