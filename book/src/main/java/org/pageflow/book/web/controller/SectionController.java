package org.pageflow.book.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.application.dto.node.SectionAttachmentUrl;
import org.pageflow.book.application.dto.node.SectionDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.TocSectionUseCase;
import org.pageflow.book.usecase.cmd.CreateSectionCmd;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.book.web.form.SectionForm;
import org.pageflow.book.web.res.UrlRes;
import org.pageflow.book.web.res.node.SectionRes;
import org.pageflow.book.web.res.node.WithContentSectionRes;
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
public class SectionController {
  private final EditTocUseCase editTocUseCase;
  private final TocSectionUseCase tocSectionUseCase;
  private final RequestContext rqcxt;

  @PostMapping("")
  @Operation(summary = "섹션 생성")
  public WithContentSectionRes createSection(
    @PathVariable UUID bookId,
    @Valid @RequestBody SectionForm.Create form
  ) {
    UID uid = rqcxt.getUid();
    CreateSectionCmd cmd = CreateSectionCmd.of(
      uid,
      bookId,
      form.getParentNodeId(),
      form.getTitle()
    );
    WithContentSectionDto withContentSectionDto = editTocUseCase.createSection(cmd);
    return WithContentSectionRes.from(withContentSectionDto);
  }

  @GetMapping("/{sectionId}")
  @Operation(summary = "섹션 조회")
  public SectionRes getSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    SectionDto result = editTocUseCase.getSection(identifier);
    return SectionRes.from(result);
  }

  @GetMapping("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public WithContentSectionRes getSectionWithContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    WithContentSectionDto result = tocSectionUseCase.getSectionWithContent(identifier);
    return WithContentSectionRes.from(result);
  }

  @PostMapping("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  public SectionRes updateSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @Valid @RequestBody SectionForm.Title req
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    SectionDto result = editTocUseCase.changeSectionTitle(identifier, req.getTitle());
    return SectionRes.from(result);
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
    editTocUseCase.deleteSection(identifier);
    return Result.ok();
  }

  @PostMapping("/{sectionId}/upload-image")
  @Operation(summary = "섹션 이미지 업로드")
  public UrlRes uploadSectionAttachmentImage(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @RequestPart MultipartFile image
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    SectionAttachmentUrl result = tocSectionUseCase.uploadAttachmentImage(identifier, image);
    return new UrlRes(result.getUrl());
  }

  @PostMapping("/{sectionId}/content")
  @Operation(summary = "섹션 내용 작성")
  public WithContentSectionRes writeContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @Valid @RequestBody SectionForm.Content form
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    WithContentSectionDto result = tocSectionUseCase.writeContent(identifier, form.getContent());
    return WithContentSectionRes.from(result);
  }

}

