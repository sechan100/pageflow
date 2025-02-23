package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.CreateFolderReq;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.port.in.BookQueries;
import org.pageflow.book.port.in.CreateFolderCmd;
import org.pageflow.book.port.in.NodeCmdUseCase;
import org.pageflow.book.port.in.UpdateFolderCmd;
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
  private final NodeCmdUseCase nodeCmdUseCase;
  private final BookQueries bookQueries;


  @Post("")
  @Operation(summary = "폴더 생성")
  public FolderDto createFolder(@PathVariable UUID bookId, @RequestBody CreateFolderReq req) {
    CreateFolderCmd cmd = CreateFolderCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    FolderDto folderDto = nodeCmdUseCase.createFolder(cmd);
    return folderDto;
  }

  @Get("/{folderId}")
  @Operation(summary = "폴더 조회")
  public FolderDto getFolder(@PathVariable UUID bookId, @PathVariable UUID folderId) {
    FolderDto folder = bookQueries.queryFolder(folderId);
    return folder;
  }

  @Post("/{folderId}")
  @Operation(summary = "폴더 업데이트")
  public FolderDto updateFolder(@PathVariable UUID bookId, @PathVariable UUID folderId, @RequestBody FolderUpdateReq req) {
    UpdateFolderCmd cmd = UpdateFolderCmd.of(
      folderId,
      req.getTitle()
    );
    FolderDto folderDto = nodeCmdUseCase.updateFolder(cmd);
    return folderDto;
  }

  @Post("/{folderId}/delete")
  @Operation(summary = "폴더 삭제")
  public void deleteFolder(@PathVariable UUID bookId, @PathVariable UUID folderId) {
    nodeCmdUseCase.deleteFolder(folderId);
  }





  @Data
  public static class FolderUpdateReq {
    @NotBlank
    private String title;
  }
}

