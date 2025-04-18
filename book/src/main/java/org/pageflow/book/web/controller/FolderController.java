package org.pageflow.book.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.cmd.CreateFolderCmd;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.book.web.form.FolderForm;
import org.pageflow.book.web.res.node.FolderRes;
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
public class FolderController {
  private final EditTocUseCase editTocUseCase;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "폴더 생성")
  public FolderRes createFolder(
    @PathVariable UUID bookId,
    @Valid @RequestBody FolderForm.Create form
  ) {
    CreateFolderCmd cmd = CreateFolderCmd.of(
      rqcxt.getUid(),
      bookId,
      form.getParentNodeId(),
      form.getTitle()
    );
    FolderDto folderDto = editTocUseCase.createFolder(cmd);
    return FolderRes.from(folderDto);
  }

  @GetMapping("/{folderId}")
  @Operation(summary = "폴더 조회")
  public FolderRes getFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      folderId
    );
    FolderDto folderDto = editTocUseCase.getFolder(identifier);
    return FolderRes.from(folderDto);
  }

  @PostMapping("/{folderId}")
  @Operation(summary = "폴더 업데이트")
  public FolderRes updateFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId,
    @Valid @RequestBody FolderForm.Update form
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      folderId
    );
    FolderDto folderDto = editTocUseCase.changeFolderTitle(identifier, form.getTitle());
    return FolderRes.from(folderDto);
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
    editTocUseCase.deleteFolder(identifier);
    return Result.ok();
  }
}

