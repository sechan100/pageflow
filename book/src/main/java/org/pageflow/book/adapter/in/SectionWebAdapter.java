package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.SectionCreateReq;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.BookQueries;
import org.pageflow.book.port.in.CreateSectionCmd;
import org.pageflow.book.port.in.NodeCmdUseCase;
import org.pageflow.book.port.in.UpdateSectionCmd;
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
@RequestMapping("/user/books/{bookId}/toc/sections")
@RequiredArgsConstructor
public class SectionWebAdapter {
  private final NodeCmdUseCase nodeCmdUseCase;
  private final BookQueries bookQueries;

  @Post("")
  @Operation(summary = "섹션 생성")
  public SectionDtoWithContent createSection(@PathVariable UUID bookId, @RequestBody SectionCreateReq req) {
    CreateSectionCmd cmd = CreateSectionCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    SectionDtoWithContent sectionDto = nodeCmdUseCase.createSection(cmd);
    return sectionDto;
  }

  @Get("/{sectionId}")
  @Operation(summary = "섹션 조회")
  public SectionDto getSection(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    SectionDto section = bookQueries.querySection(sectionId);
    return section;
  }

  @Get("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public SectionDtoWithContent getSectionWithContent(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    SectionDtoWithContent section = bookQueries.querySectionWithContent(sectionId);
    return section;
  }

  @Post("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  public SectionDtoWithContent updateSection(@PathVariable UUID bookId, @PathVariable UUID sectionId, @RequestBody SectionUpdateReq req) {
    UpdateSectionCmd cmd = UpdateSectionCmd.of(
      bookId,
      req.getTitle(),
      req.getContent()
    );
    SectionDtoWithContent section = nodeCmdUseCase.updateSection(cmd);
    return section;
  }

  @Post("/{sectionId}/delete")
  @Operation(summary = "섹션 삭제")
  public void deleteSection(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    nodeCmdUseCase.deleteSection(sectionId);
  }



  @Data
  public static class SectionUpdateReq {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
  }
}

