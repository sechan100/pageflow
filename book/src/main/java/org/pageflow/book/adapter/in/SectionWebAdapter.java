package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.SectionForm;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.NodeId;
import org.pageflow.book.application.dto.SectionAttachmentUrl;
import org.pageflow.book.application.dto.SectionDto;
import org.pageflow.book.application.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.SectionWriteUseCase;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.NodeAccessIds;
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
  private final TocUseCase tocUseCase;
  private final SectionWriteUseCase sectionWriteUseCase;
  private final RequestContext rqcxt;

  @PostMapping("")
  @Operation(summary = "섹션 생성")
  public Result<SectionDtoWithContent> createSection(
    @PathVariable UUID bookId,
    @Valid @RequestBody SectionForm.Create form
  ) {
    UID uid = rqcxt.getUid();
    CreateSectionCmd cmd = CreateSectionCmd.withTitle(
      uid,
      new BookId(bookId),
      new NodeId(form.getParentNodeId()),
      form.getTitle()
    );
    Result<SectionDtoWithContent> result = tocUseCase.createSection(cmd);
    return result;
  }

  @GetMapping("/{sectionId}")
  @Operation(summary = "섹션 조회")
  public Result<SectionDto> getSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    NodeAccessIds ids = new NodeAccessIds(
      uid,
      new BookId(bookId),
      new NodeId(sectionId)
    );
    Result<SectionDto> result = tocUseCase.getSection(ids);
    return result;
  }

  @GetMapping("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public Result<SectionDtoWithContent> getSectionWithContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    NodeAccessIds ids = new NodeAccessIds(
      uid,
      new BookId(bookId),
      new NodeId(sectionId)
    );
    Result<SectionDtoWithContent> result = sectionWriteUseCase.getSectionWithContent(ids);
    return result;
  }

  @PostMapping("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  public Result<SectionDto> updateSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @Valid @RequestBody SectionForm.Title req
  ) {
    UID uid = rqcxt.getUid();
    NodeAccessIds ids = new NodeAccessIds(
      uid,
      new BookId(bookId),
      new NodeId(sectionId)
    );
    Result<SectionDto> result = tocUseCase.changeSectionTitle(ids, req.getTitle());
    return result;
  }

  @DeleteMapping("/{sectionId}")
  @Operation(summary = "섹션 삭제")
  public Result deleteSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    UID uid = rqcxt.getUid();
    NodeAccessIds ids = new NodeAccessIds(
      uid,
      new BookId(bookId),
      new NodeId(sectionId)
    );
    return tocUseCase.deleteSection(ids);
  }

  @PostMapping("/{sectionId}/upload-image")
  @Operation(summary = "섹션 이미지 업로드")
  public Result<SectionAttachmentUrl> uploadSectionAttachmentImage(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @RequestPart MultipartFile image
  ) {
    UID uid = rqcxt.getUid();
    NodeAccessIds ids = new NodeAccessIds(
      uid,
      new BookId(bookId),
      new NodeId(sectionId)
    );
    return sectionWriteUseCase.uploadAttachmentImage(ids, image);
  }

  @PostMapping("/{sectionId}/content")
  @Operation(summary = "섹션 내용 작성")
  public Result<SectionDtoWithContent> writeContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @Valid @RequestBody SectionForm.Content form
  ) {
    UID uid = rqcxt.getUid();
    NodeAccessIds ids = new NodeAccessIds(
      uid,
      new BookId(bookId),
      new NodeId(sectionId)
    );
    return sectionWriteUseCase.writeContent(ids, form.getContent());
  }

}

