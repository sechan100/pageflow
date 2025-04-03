package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.CreateFolderReq;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.UpdateFolderCmd;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.user.UID;
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
  private final TocUseCase tocUseCase;
  private final BookAccessPermitter bookAccessPermitter;
  private final RequestContext rqcxt;


  @PostMapping("")
  @Operation(summary = "폴더 생성")
  public FolderDto createFolder(
    @PathVariable UUID bookId,
    @RequestBody CreateFolderReq req
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    CreateFolderCmd cmd = CreateFolderCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    FolderDto folderDto = tocUseCase.createFolder(bookId, cmd);
    return folderDto;
  }

  @GetMapping("/{folderId}")
  @Operation(summary = "폴더 조회")
  public FolderDto getFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    FolderDto folder = tocUseCase.getFolder(bookId, folderId);
    return folder;
  }

  @PostMapping("/{folderId}")
  @Operation(summary = "폴더 업데이트")
  public FolderDto updateFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId,
    @RequestBody FolderUpdateReq req
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    UpdateFolderCmd cmd = UpdateFolderCmd.of(
      folderId,
      req.getTitle()
    );
    FolderDto folderDto = tocUseCase.updateFolder(bookId, cmd);
    return folderDto;
  }


  @DeleteMapping("/{folderId}")
  @Operation(summary = "폴더 삭제")
  public void deleteFolder(
    @PathVariable UUID bookId,
    @PathVariable UUID folderId
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    tocUseCase.deleteFolder(bookId, folderId);
  }


  @Data
  public static class FolderUpdateReq {
    @NotBlank
    private String title;
  }
}

