package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.SectionForm;
import org.pageflow.book.application.dto.SectionAttachmentUrl;
import org.pageflow.book.application.dto.SectionDto;
import org.pageflow.book.application.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.EditTocUseCase;
import org.pageflow.book.port.in.SectionWriteUseCase;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.NodeIdentifier;
import org.pageflow.common.api.RequestContext;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
@RestController
@RequestMapping("/user/books/{bookId}/toc/sections")
@RequiredArgsConstructor
public class SectionWebAdapter {
  private final EditTocUseCase editTocUseCase;
  private final SectionWriteUseCase sectionWriteUseCase;
  private final RequestContext rqcxt;

  @PostMapping("")
  @Operation(summary = "섹션 생성")
  public Result<SectionDtoWithContent> createSection(
    @PathVariable UUID bookId,
    @Valid @RequestBody SectionForm.Create form
  ) {
    UID uid = rqcxt.getUid();
    CreateSectionCmd cmd = new CreateSectionCmd(
      uid,
      bookId,
      form.getParentNodeId(),
      form.getTitle()
    );
    Result<SectionDtoWithContent> result = editTocUseCase.createSection(cmd);
    return result;
  }

  @GetMapping("/{sectionId}")
  @Operation(summary = "섹션 조회")
  public Result<SectionDto> getSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    Result<SectionDto> result = editTocUseCase.getSection(identifier);
    return result;
  }

  @GetMapping("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public Result<SectionDtoWithContent> getSectionWithContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    Result<SectionDtoWithContent> result = sectionWriteUseCase.getSectionWithContent(identifier);
    return result;
  }

  @PostMapping("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  public Result<SectionDto> updateSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @Valid @RequestBody SectionForm.Title req
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    Result<SectionDto> result = editTocUseCase.changeSectionTitle(identifier, req.getTitle());
    return result;
  }

  @DeleteMapping("/{sectionId}")
  @Operation(summary = "섹션 삭제")
  public Result deleteSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    return editTocUseCase.deleteSection(identifier);
  }

  @PostMapping("/{sectionId}/upload-image")
  @Operation(summary = "섹션 이미지 업로드")
  public Result<SectionAttachmentUrl> uploadSectionAttachmentImage(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @RequestPart MultipartFile image
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    return sectionWriteUseCase.uploadAttachmentImage(identifier, image);
  }

  @PostMapping("/{sectionId}/content")
  @Operation(summary = "섹션 내용 작성")
  public Result<SectionDtoWithContent> writeContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @Valid @RequestBody SectionForm.Content form
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    return sectionWriteUseCase.writeContent(identifier, form.getContent());
  }

}

