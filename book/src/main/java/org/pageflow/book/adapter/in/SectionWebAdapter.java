package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.aop.SetBookPermission;
import org.pageflow.book.adapter.in.request.SectionCreateReq;
import org.pageflow.book.application.BookId;
import org.pageflow.book.dto.SectionAttachmentUrl;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.SectionWriteUseCase;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.UpdateSectionCmd;
import org.pageflow.common.result.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
@Validated
@RestController
@RequestMapping("/user/books/{bookId}/toc/sections")
@RequiredArgsConstructor
public class SectionWebAdapter {
  private final TocUseCase tocUseCase;
  private final SectionWriteUseCase sectionWriteUseCase;

  @PostMapping("")
  @Operation(summary = "섹션 생성")
  @SetBookPermission
  public SectionDtoWithContent createSection(
    @PathVariable @BookId UUID bookId,
    @RequestBody SectionCreateReq req
  ) {
    CreateSectionCmd cmd = CreateSectionCmd.withTitle(
      bookId,
      req.getParentNodeId(),
      req.getTitle()
    );
    SectionDtoWithContent sectionDto = tocUseCase.createSection(bookId, cmd);
    return sectionDto;
  }

  @GetMapping("/{sectionId}")
  @Operation(summary = "섹션 조회")
  @SetBookPermission
  public SectionDto getSection(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId
  ) {
    SectionDto section = tocUseCase.getSection(bookId, sectionId);
    return section;
  }

  @GetMapping("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  @SetBookPermission
  public SectionDtoWithContent getSectionWithContent(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId
  ) {
    SectionDtoWithContent section = sectionWriteUseCase.getSectionWithContent(bookId, sectionId);
    return section;
  }

  @PostMapping("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  @SetBookPermission
  public Result<SectionDto> updateSection(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId,
    @RequestBody SectionUpdateReq req
  ) {
    UpdateSectionCmd cmd = UpdateSectionCmd.createCmd(
      sectionId,
      req.getTitle()
    );
    Result<SectionDto> result = sectionWriteUseCase.updateSection(bookId, cmd);
    return result;
  }

  @DeleteMapping("/{sectionId}")
  @Operation(summary = "섹션 삭제")
  @SetBookPermission
  public void deleteSection(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId
  ) {
    tocUseCase.deleteSection(bookId, sectionId);
  }

  @PostMapping("/{sectionId}/upload-image")
  @Operation(summary = "섹션 이미지 업로드")
  @SetBookPermission
  public Result<SectionAttachmentUrl> uploadSectionAttachmentImage(
    @PathVariable @BookId UUID bookId,
    @PathVariable UUID sectionId,
    @RequestPart MultipartFile image
  ) {
    return sectionWriteUseCase.uploadAttachmentImage(bookId, sectionId, image);
  }


  @Data
  public static class SectionUpdateReq {
    @NotBlank
    private String title;
  }
}

