package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.SectionCreateReq;
import org.pageflow.book.adapter.in.form.SectionForm;
import org.pageflow.book.dto.SectionAttachmentUrl;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.BookAccessPermitter;
import org.pageflow.book.port.in.SectionWriteUseCase;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.UpdateSectionCmd;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
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
  private final BookAccessPermitter bookAccessPermitter;
  private final RequestContext rqcxt;

  @PostMapping("")
  @Operation(summary = "섹션 생성")
  public SectionDtoWithContent createSection(
    @PathVariable UUID bookId,
    @RequestBody SectionCreateReq req
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
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
  public SectionDto getSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    SectionDto section = tocUseCase.getSection(bookId, sectionId);
    return section;
  }

  @GetMapping("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public SectionDtoWithContent getSectionWithContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    SectionDtoWithContent section = sectionWriteUseCase.getSectionWithContent(bookId, sectionId);
    return section;
  }

  @PostMapping("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  public Result<SectionDto> updateSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @RequestBody SectionUpdateReq req
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    UpdateSectionCmd cmd = UpdateSectionCmd.createCmd(
      sectionId,
      req.getTitle()
    );
    Result<SectionDto> result = sectionWriteUseCase.updateSection(bookId, cmd);
    return result;
  }

  @DeleteMapping("/{sectionId}")
  @Operation(summary = "섹션 삭제")
  public void deleteSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    tocUseCase.deleteSection(bookId, sectionId);
  }

  @PostMapping("/{sectionId}/upload-image")
  @Operation(summary = "섹션 이미지 업로드")
  public Result<SectionAttachmentUrl> uploadSectionAttachmentImage(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @RequestPart MultipartFile image
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    return sectionWriteUseCase.uploadAttachmentImage(bookId, sectionId, image);
  }

  @PostMapping("/{sectionId}/content")
  @Operation(summary = "섹션 내용 작성")
  public Result<SectionDtoWithContent> writeContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @RequestBody SectionForm.Content form
  ) {
    UID uid = rqcxt.getUid();
    bookAccessPermitter.setPermission(bookId, uid);
    return sectionWriteUseCase.writeContent(bookId, sectionId, form.getContent());
  }


  @Data
  public static class SectionUpdateReq {
    @NotBlank
    private String title;
  }
}

