package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.dto.SectionAttachmentUrl;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.LexicalHtmlSectionContent;
import org.pageflow.book.port.in.SectionWriteUseCase;
import org.pageflow.book.port.in.cmd.UpdateSectionCmd;
import org.pageflow.book.port.out.jpa.SectionPersistencePort;
import org.pageflow.common.permission.PermissionRequired;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.model.FileUploadCmd;
import org.pageflow.file.service.FileService;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SectionWriteService implements SectionWriteUseCase {
  private final SectionPersistencePort sectionPersistencePort;
  private final FileService fileService;

  @Override
  @PermissionRequired(
    actions = {"READ"},
    permissionType = BookPermission.class
  )
  public SectionDtoWithContent getSectionWithContent(@BookId UUID bookId, UUID sectionId) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    return SectionDtoWithContent.from(section);
  }

  @Override
  @PermissionRequired(
    actions = {"EDIT"},
    permissionType = BookPermission.class
  )
  public Result<SectionDto> updateSection(@BookId UUID bookId, UpdateSectionCmd cmd) {
    Section section = sectionPersistencePort.findById(cmd.getId()).get();
    section.changeTitle(cmd.getTitle().getValue());
    return Result.success(SectionDto.from(section));
  }

  @Override
  @PermissionRequired(
    actions = {"EDIT"},
    permissionType = BookPermission.class
  )
  public Result<SectionDtoWithContent> writeContent(@BookId UUID bookId, UUID sectionId, LexicalHtmlSectionContent content) {

    return null;
  }

  /**
   * @code DATA_NOT_FOUND: 섹션을 찾을 수 없는 경우
   * @code FIELD_VALIDATION_ERROR: file 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: 파일 업로드에 실패한 경우
   * @code 그 외 FileValidator에 따라서 다양한 ResultCode 발생 가능
   */
  @Override
  @PermissionRequired(
    actions = {"EDIT"},
    permissionType = BookPermission.class
  )
  public Result<SectionAttachmentUrl> uploadAttachmentImage(@BookId UUID bookId, UUID sectionId, MultipartFile file) {
    boolean isSectionExist = sectionPersistencePort.existsById(sectionId);
    if(!isSectionExist) {
      return Result.of(CommonCode.DATA_NOT_FOUND, "섹션을 찾을 수 없습니다.");
    }

    Result<FileUploadCmd> cmd = FileUploadCmd.createCmd(
      file,
      sectionId.toString(),
      FileType.BOOK_SECTION_ATTACHMENT_IMAGE
    );
    if(cmd.isFailure()) {
      return (Result) cmd;
    }

    Result<FilePath> uploadResult = fileService.upload(cmd.getSuccessData());
    if(uploadResult.isFailure()) {
      return (Result) uploadResult;
    }

    SectionAttachmentUrl attachmentUrl = new SectionAttachmentUrl(uploadResult.getSuccessData().getWebUrl());
    return Result.success(attachmentUrl);
  }
}
