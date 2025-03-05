package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.NodeRelocateReq;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.TocNodeUseCase;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


  @GetMapping("")
  @Operation(summary = "책 목차 조회")
  @SetBookPermission
  public TocDto.Toc getToc(@PathVariable @BookId UUID bookId) {
    TocDto.Toc toc = tocUsecase.loadToc(bookId);
    return toc;
  }

  @PostMapping("/relocate-node")
  @Operation(summary = "목차 노드 재배치")
  @SetBookPermission
  public void relocateNode(
    @PathVariable @BookId UUID bookId,
    @RequestBody NodeRelocateReq req
  ){
    RelocateNodeCmd cmd = new RelocateNodeCmd(
      bookId,
      req.getTargetNodeId(),
      req.getDestFolderId(),
      req.getDestIndex()
    );
    tocNodeUseCase.relocateNode(bookId, cmd);
  }

}
