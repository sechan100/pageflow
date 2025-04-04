package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.FolderForm;
import org.pageflow.book.application.dto.FolderDto;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequestMapping("/user/books/{bookId}/toc/folders")
@RequiredArgsConstructor
public class FolderWebAdapter {
  private final TocUseCase tocUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "폴더 생성")
  public Result<FolderDto> createFolder(
    @PathVariable UUID bookId,
    @Valid @RequestBody FolderForm.Create form
  ) {
    UID uid = rqcxt.getUid();
    CreateFolderCmd cmd = CreateFolderCmd.withTitle(
      uid,
      form.getParentNodeId(),
      form.getTitle()
    );
    Result<FolderDto> result = tocUseCase.createFolder(cmd);
    return result;
  }

  @GetMapping("/{folderId}")
  @Operation(summary = "폴더 조회")
  public Result<FolderDto> getFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    UID uid = rqcxt.getUid();
    Result<FolderDto> result = tocUseCase.getFolder(uid, folderId);
    return result;
  }

  @PostMapping("/{folderId}")
  @Operation(summary = "폴더 업데이트")
  public Result<FolderDto> updateFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId,
    @Valid @RequestBody FolderForm.Update form
  ) {
    UID uid = rqcxt.getUid();
    Result<FolderDto> result = tocUseCase.changeFolderTitle(uid, folderId, form.getTitle());
    return result;
  }


  @DeleteMapping("/{folderId}")
  @Operation(summary = "폴더 삭제")
  public Result deleteFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    UID uid = rqcxt.getUid();
    Result result = tocUseCase.deleteFolder(uid, folderId);
    return result;
  }
}

