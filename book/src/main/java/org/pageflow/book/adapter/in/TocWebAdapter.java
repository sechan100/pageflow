package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.NodeReplaceReq;
import org.pageflow.book.adapter.in.request.SectionCreateReq;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.CreateSectionCmd;
import org.pageflow.book.port.in.NodeCmdUseCase;
import org.pageflow.book.port.in.ReplaceNodeCmd;
import org.pageflow.book.port.in.TocUseCase;
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
  private final NodeCmdUseCase nodeCmdUseCase;


  @Get("/user/books/{bookId}/toc")
  @Operation(summary = "책 목차 조회")
  public TocDto.Toc getToc(@PathVariable UUID bookId) {
    TocDto.Toc toc = tocUsecase.loadToc(bookId);
    return toc;
  }

  @Post("/user/books/{bookId}/toc/replace-node")
  @Operation(summary = "목차 노드 재배치")
  public void reorder(@PathVariable UUID bookId, @RequestBody NodeReplaceReq req) {
    ReplaceNodeCmd cmd = new ReplaceNodeCmd(
      bookId,
      req.getTargetNodeId(),
      req.getDestFolderId(),
      req.getDestIndex()
    );
    tocUsecase.replaceNode(cmd);
  }



  @Post("/user/books/{bookId}/toc/create-section")
  @Operation(summary = "섹션 생성")
  public SectionDtoWithContent createSection(@PathVariable UUID bookId, @RequestBody SectionCreateReq req) {
    CreateSectionCmd cmd = new CreateSectionCmd(
      bookId,
      req.getParentNodeId(),
      NodeTitle.of(req.getTitle())
    );
    SectionDtoWithContent sectionDto = nodeCmdUseCase.createSection(cmd);
    return sectionDto;
  }


}
