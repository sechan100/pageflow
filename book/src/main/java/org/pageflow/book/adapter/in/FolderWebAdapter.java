package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.FolderForm;
import org.pageflow.book.adapter.in.res.node.FolderRes;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.port.in.EditTocUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.NodeIdentifier;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequestMapping("/user/books/{bookId}/toc/folders")
@RequiredArgsConstructor
public class FolderWebAdapter {
  private final EditTocUseCase editTocUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "폴더 생성")
  public Result<FolderRes> createFolder(
    @PathVariable UUID bookId,
    @Valid @RequestBody FolderForm.Create form
  ) {
    CreateFolderCmd cmd = new CreateFolderCmd(
      rqcxt.getUid(),
      bookId,
      form.getParentNodeId(),
      form.getTitle()
    );
    Result<FolderDto> result = editTocUseCase.createFolder(cmd);
    if(result.isFailure()) {
      return (Result) result;
    }
    FolderRes res = new FolderRes(result.getSuccessData());
    return Result.success(res);
  }

  @GetMapping("/{folderId}")
  @Operation(summary = "폴더 조회")
  public Result<FolderRes> getFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      folderId
    );
    Result<FolderDto> result = editTocUseCase.getFolder(identifier);
    if(result.isFailure()) {
      return (Result) result;
    }
    FolderRes res = new FolderRes(result.getSuccessData());
    return Result.success(res);
  }

  @PostMapping("/{folderId}")
  @Operation(summary = "폴더 업데이트")
  public Result<FolderRes> updateFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId,
    @Valid @RequestBody FolderForm.Update form
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      folderId
    );
    Result<FolderDto> result = editTocUseCase.changeFolderTitle(identifier, form.getTitle());
    if(result.isFailure()) {
      return (Result) result;
    }
    FolderRes res = new FolderRes(result.getSuccessData());
    return Result.success(res);
  }


  @DeleteMapping("/{folderId}")
  @Operation(summary = "폴더 삭제")
  public Result deleteFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      folderId
    );
    Result result = editTocUseCase.deleteFolder(identifier);
    return result;
  }
}

