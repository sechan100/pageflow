package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.CreateFolderReq;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.port.in.TocNodeUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.UpdateFolderCmd;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequestMapping("/user/books/{bookId}/toc/folders")
@RequiredArgsConstructor
public class FolderWebAdapter {
  private final TocNodeUseCase tocNodeUseCase;


  @PostMapping("")
  @Operation(summary = "폴더 생성")
  @SetBookPermission
  public FolderDto createFolder(
    @PathVariable @BookId UUID bookId,
    @RequestBody CreateFolderReq req
  ){
    CreateFolderCmd cmd = CreateFolderCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    FolderDto folderDto = tocNodeUseCase.createFolder(bookId, cmd);
    return folderDto;
  }

  @GetMapping("/{folderId}")
  @Operation(summary = "폴더 조회")
  @SetBookPermission
  public FolderDto getFolder(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID folderId
  ){
    FolderDto folder = tocNodeUseCase.queryFolder(bookId, folderId);
    return folder;
  }

  @PostMapping("/{folderId}")
  @Operation(summary = "폴더 업데이트")
  @SetBookPermission
  public FolderDto updateFolder(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID folderId,
    @RequestBody FolderUpdateReq req
  ){
    UpdateFolderCmd cmd = UpdateFolderCmd.of(
      folderId,
      req.getTitle()
    );
    FolderDto folderDto = tocNodeUseCase.updateFolder(bookId, cmd);
    return folderDto;
  }


  @DeleteMapping("/{folderId}")
  @Operation(summary = "폴더 삭제")
  @SetBookPermission
  public void deleteFolder(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID folderId
  ){
    tocNodeUseCase.deleteFolder(bookId, folderId);
  }



  @Data
  public static class FolderUpdateReq {
    @NotBlank
    private String title;
  }
}

