package org.pageflow.book.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.cmd.RelocateNodeCmd;
import org.pageflow.book.web.form.NodeRelocateForm;
import org.pageflow.book.web.res.node.TocRes;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequestMapping("/user/books/{bookId}/toc")
@RequiredArgsConstructor
public class TocWebAdapter {
  private final EditTocUseCase editTocUseCase;
  private final RequestContext rqcxt;


  @GetMapping("")
  @Operation(summary = "책 목차 조회")
  public Result<TocRes> getToc(@PathVariable UUID bookId) {
    UID uid = rqcxt.getUid();
    Result<TocDto> result = editTocUseCase.getToc(uid, bookId);
    if(result.isFailure()) {
      return (Result) result;
    }
    return Result.SUCCESS(TocRes.from(result.get()));
  }

  @PostMapping("/relocate-node")
  @Operation(summary = "목차 노드 재배치")
  public Result relocateNode(
    @PathVariable UUID bookId,
    @RequestBody NodeRelocateForm form
  ) {
    UID uid = rqcxt.getUid();
    RelocateNodeCmd cmd = new RelocateNodeCmd(
      uid,
      bookId,
      form.getTargetNodeId(),
      form.getDestFolderId(),
      form.getDestIndex()
    );
    return editTocUseCase.relocateNode(cmd);
  }
}
