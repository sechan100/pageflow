package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.CreateFolderReq;
import org.pageflow.book.adapter.in.request.CreateSectionReq;
import org.pageflow.book.adapter.in.request.NodeReplaceReq;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.*;
import org.pageflow.common.utility.Get;
import org.pageflow.common.utility.Post;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequiredArgsConstructor
public class TocWebAdapter {
  private final TocUseCase tocUsecase;
  private final NodeCrudUseCase nodeCrudUseCase;


  @Get("/user/books/{bookId}/toc")
  @Operation(summary = "책 목차 조회")
  public TocDto.Toc getToc(@PathVariable UUID bookId) {
    TocDto.Toc toc = tocUsecase.loadToc(bookId);
    return toc;
  }

  @Post("/user/books/{bookId}/toc/replace-node")
  @Operation(summary = "목차 노드 재배치")
  public void reorder(@PathVariable UUID bookId, @RequestBody NodeReplaceReq req) {
    NodeReplaceCmd cmd = new NodeReplaceCmd(
      bookId,
      req.getTargetNodeId(),
      req.getDestFolderId(),
      req.getDestIndex()
    );
    tocUsecase.replaceNode(cmd);
  }

  @Post("/user/books/{bookId}/toc/create-folder")
  @Operation(summary = "폴더 생성")
  public FolderDto createFolder(@PathVariable UUID bookId, @RequestBody CreateFolderReq req) {
    NodeTitle title = NodeTitle.validOf(req.getTitle());
    CreateFolderCmd cmd = new CreateFolderCmd(
      bookId,
      req.getParentNodeId(),
      title
    );
    FolderDto folderDto = nodeCrudUseCase.createFolder(cmd);
    return folderDto;
  }

  @Post("/user/books/{bookId}/toc/create-section")
  @Operation(summary = "섹션 생성")
  public SectionDtoWithContent createSection(@PathVariable UUID bookId, @RequestBody CreateSectionReq req) {
    CreateSectionCmd cmd = new CreateSectionCmd(
      bookId,
      req.getParentNodeId(),
      NodeTitle.validOf(req.getTitle())
    );
    SectionDtoWithContent sectionDto = nodeCrudUseCase.createSection(cmd);
    return sectionDto;
  }
}
