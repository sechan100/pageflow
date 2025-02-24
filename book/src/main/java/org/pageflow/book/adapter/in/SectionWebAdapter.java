package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.request.SectionCreateReq;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
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
@RequestMapping("/user/books/{bookId}/toc/sections")
@RequiredArgsConstructor
public class SectionWebAdapter {
  private final NodeCrudUseCase nodeCrudUseCase;
  private final BookAccessPermitter permitter;
  private final RequestContext rqcx;

  @Post("")
  @Operation(summary = "섹션 생성")
  public SectionDtoWithContent createSection(@PathVariable UUID bookId, @RequestBody SectionCreateReq req) {
    CreateSectionCmd cmd = CreateSectionCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    SectionDtoWithContent sectionDto = nodeCrudUseCase.createSection(permission, cmd);
    return sectionDto;
  }

  @Get("/{sectionId}")
  @Operation(summary = "섹션 조회")
  public SectionDto getSection(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    SectionDto section = nodeCrudUseCase.querySection(permission, sectionId);
    return section;
  }

  @Get("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public SectionDtoWithContent getSectionWithContent(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    SectionDtoWithContent section = nodeCrudUseCase.querySectionWithContent(permission, sectionId);
    return section;
  }

  @Post("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  public SectionDtoWithContent updateSection(@PathVariable UUID bookId, @PathVariable UUID sectionId, @RequestBody SectionUpdateReq req) {
    UpdateSectionCmd cmd = UpdateSectionCmd.of(
      sectionId,
      req.getTitle(),
      req.getContent()
    );
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    SectionDtoWithContent section = nodeCrudUseCase.updateSection(permission, cmd);
    return section;
  }

  @Post("/{sectionId}/delete")
  @Operation(summary = "섹션 삭제")
  public void deleteSection(@PathVariable UUID bookId, @PathVariable UUID sectionId) {
    BookPermission permission = permitter.getAuthorPermission(bookId, rqcx.getUid());
    nodeCrudUseCase.deleteSection(permission, sectionId);
  }



  @Data
  public static class SectionUpdateReq {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
  }
}

