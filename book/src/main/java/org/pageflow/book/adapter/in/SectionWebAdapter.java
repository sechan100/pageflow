package org.pageflow.book.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.book.adapter.in.form.SectionForm;
import org.pageflow.book.adapter.in.res.UrlRes;
import org.pageflow.book.adapter.in.res.node.SectionRes;
import org.pageflow.book.adapter.in.res.node.WithContentSectionRes;
import org.pageflow.book.application.dto.node.SectionAttachmentUrl;
import org.pageflow.book.application.dto.node.SectionDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
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
  public Result<WithContentSectionRes> createSection(
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
    Result<WithContentSectionDto> result = editTocUseCase.createSection(cmd);
    if(result.isFailure()) {
      return (Result) result;
    }
    WithContentSectionRes res = new WithContentSectionRes(result.getSuccessData());
    return Result.success(res);
  }

  @GetMapping("/{sectionId}")
  @Operation(summary = "섹션 조회")
  public Result<SectionRes> getSection(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    Result<SectionDto> result = editTocUseCase.getSection(identifier);
    if(result.isFailure()) {
      return (Result) result;
    }
    SectionRes res = new SectionRes(result.getSuccessData());
    return Result.success(res);
  }

  @GetMapping("/{sectionId}/content")
  @Operation(summary = "섹션을 내용과 함께 조회")
  public Result<WithContentSectionRes> getSectionWithContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    Result<WithContentSectionDto> result = sectionWriteUseCase.getSectionWithContent(identifier);
    if(result.isFailure()) {
      return (Result) result;
    }
    WithContentSectionRes res = new WithContentSectionRes(result.getSuccessData());
    return Result.success(res);
  }

  @PostMapping("/{sectionId}")
  @Operation(summary = "섹션 업데이트")
  public Result<SectionRes> updateSection(
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
    if(result.isFailure()) {
      return (Result) result;
    }
    SectionRes res = new SectionRes(result.getSuccessData());
    return Result.success(res);
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
  public Result<UrlRes> uploadSectionAttachmentImage(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @RequestPart MultipartFile image
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    Result<SectionAttachmentUrl> result = sectionWriteUseCase.uploadAttachmentImage(identifier, image);
    if(result.isFailure()) {
      return (Result) result;
    }
    UrlRes res = new UrlRes(result.getSuccessData().getUrl());
    return Result.success(res);
  }

  @PostMapping("/{sectionId}/content")
  @Operation(summary = "섹션 내용 작성")
  public Result<WithContentSectionRes> writeContent(
    @PathVariable UUID bookId,
    @PathVariable UUID sectionId,
    @Valid @RequestBody SectionForm.Content form
  ) {
    NodeIdentifier identifier = new NodeIdentifier(
      rqcxt.getUid(),
      bookId,
      sectionId
    );
    Result<WithContentSectionDto> result = sectionWriteUseCase.writeContent(identifier, form.getContent());
    if(result.isFailure()) {
      return (Result) result;
    }
    WithContentSectionRes res = new WithContentSectionRes(result.getSuccessData());
    return Result.success(res);
  }

}

