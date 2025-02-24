package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.CreateFolderReq;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.port.in.*;
import org.pageflow.common.api.RequestContext;
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
@RequestMapping("/user/books/{bookId}/toc/folders")
@RequiredArgsConstructor
public class FolderWebAdapter {
  private final NodeCrudUseCase nodeCrudUseCase;
  private final BookAccessPermitter permitter;
  private final RequestContext rqcx;


  @Post("")
  @Operation(summary = "폴더 생성")
  public FolderDto createFolder(@PathVariable UUID bookId, @RequestBody CreateFolderReq req) {
    CreateFolderCmd cmd = CreateFolderCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    FolderDto folderDto = nodeCrudUseCase.createFolder(permission, cmd);
    return folderDto;
  }

  @Get("/{folderId}")
  @Operation(summary = "폴더 조회")
  public FolderDto getFolder(@PathVariable UUID bookId, @PathVariable UUID folderId) {
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    FolderDto folder = nodeCrudUseCase.queryFolder(permission, folderId);
    return folder;
  }

  @Post("/{folderId}")
  @Operation(summary = "폴더 업데이트")
  public FolderDto updateFolder(@PathVariable UUID bookId, @PathVariable UUID folderId, @RequestBody FolderUpdateReq req) {
    UpdateFolderCmd cmd = UpdateFolderCmd.of(
      folderId,
      req.getTitle()
    );
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    FolderDto folderDto = nodeCrudUseCase.updateFolder(permission, cmd);
    return folderDto;
  }

  @Post("/{folderId}/delete")
  @Operation(summary = "폴더 삭제")
  public void deleteFolder(@PathVariable UUID bookId, @PathVariable UUID folderId) {
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    nodeCrudUseCase.deleteFolder(permission, folderId);
  }





  @Data
  public static class FolderUpdateReq {
    @NotBlank
    private String title;
  }
}

