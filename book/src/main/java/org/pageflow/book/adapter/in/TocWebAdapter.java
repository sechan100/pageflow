package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.NodeRelocateReq;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
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
  private final TocUseCase tocUseCase;
  private final BookAccessPermitter bookAccessPermitter;
  private final RequestContext rqcxt;


  @GetMapping("")
  @Operation(summary = "책 목차 조회")
  public TocDto.Toc getToc(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    TocDto.Toc toc = tocUseCase.getToc(bookId);
    return toc;
  }

  @PostMapping("/relocate-node")
  @Operation(summary = "목차 노드 재배치")
  public Result relocateNode(
    @PathVariable UUID bookId,
    @RequestBody NodeRelocateReq req
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    RelocateNodeCmd cmd = new RelocateNodeCmd(
      bookId,
      req.getTargetNodeId(),
      req.getDestFolderId(),
      req.getDestIndex()
    );
    return tocUseCase.relocateNode(bookId, cmd);
  }
}
