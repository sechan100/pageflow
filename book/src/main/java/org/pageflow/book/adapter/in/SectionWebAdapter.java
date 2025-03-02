package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.SectionCreateReq;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.NodeCrudUseCase;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.UpdateSectionCmd;
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
  private final NodeCrudUseCase nodeCrudUseCase;

  @Post("")
  @Operation(summary = "섹션 생성")
  @SetBookPermission
  public SectionDtoWithContent createSection(
    @PathVariable @BookId UUID bookId,
    @RequestBody SectionCreateReq req
  ){
    CreateSectionCmd cmd = CreateSectionCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    SectionDtoWithContent sectionDto = nodeCrudUseCase.createSection(bookId, cmd);
    return sectionDto;
  }

  @Get("/{sectionId}")
  @Operation(summary = "섹션 조회")
  @SetBookPermission
  public SectionDto getSection(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId
  ){
    SectionDto section = nodeCrudUseCase.querySection(bookId, sectionId);
    return section;
  }

  @Get("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  @SetBookPermission
  public SectionDtoWithContent getSectionWithContent(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId
  ){
    SectionDtoWithContent section = nodeCrudUseCase.querySectionWithContent(bookId, sectionId);
    return section;
  }

  @Post("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  @SetBookPermission
  public SectionDtoWithContent updateSection(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId,
    @RequestBody SectionUpdateReq req
  ){
    UpdateSectionCmd cmd = UpdateSectionCmd.of(
      sectionId,
      req.getTitle(),
      req.getContent()
    );
    SectionDtoWithContent section = nodeCrudUseCase.updateSection(bookId, cmd);
    return section;
  }

  @Post("/{sectionId}/delete")
  @Operation(summary = "섹션 삭제")
  @SetBookPermission
  public void deleteSection(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId
  ){
    nodeCrudUseCase.deleteSection(bookId, sectionId);
  }



  @Data
  public static class SectionUpdateReq {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
  }
}

