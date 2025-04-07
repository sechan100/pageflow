package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.SectionAttachmentUrl;
import org.pageflow.book.application.dto.SectionDtoWithContent;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.SectionHtmlContent;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.NodeContent;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.domain.toc.TocSection;
import org.pageflow.book.port.in.cmd.NodeIdentifier;
import org.pageflow.book.port.out.EditTocPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.result.Result;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.model.FileUploadCmd;
import org.pageflow.file.service.FileService;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SectionWriteUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final EditTocPort editTocPort;
  private final FileService fileService;

  /**
   * 작가 권한으로 section을 읽어온다.
   * 독자의 section read를 위해서는 다른 메소드를 사용.
   *
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result<SectionDtoWithContent> getSectionWithContent(NodeIdentifier identifier) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    TocSection section = editTocPort.loadEditableSection(book, identifier.getNodeId()).get();
    // 권한 검사 =========================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result checkWriteAuthorityRes = accessGranter.grant(BookAccess.WRITE);
    if(checkWriteAuthorityRes.isFailure()) {
      return checkWriteAuthorityRes;
    }
    return Result.success(SectionDtoWithContent.from(section));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code DATA_NOT_FOUND: 섹션을 찾을 수 없는 경우
   */
  public Result<SectionDtoWithContent> writeContent(NodeIdentifier identifier, String content) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    TocSection section = editTocPort.loadEditableSection(book, identifier.getNodeId()).get();
    // 권한 검사 ==================================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result checkWriteAuthorityRes = accessGranter.grant(BookAccess.WRITE);
    if(checkWriteAuthorityRes.isFailure()) return checkWriteAuthorityRes;
    // 내용 작성 ===================================================
    SectionHtmlContent html = new SectionHtmlContent(content);
    if(!html.getIsSanitizationConsistent()) {
      log.warn("""
            Section({})의 content의 html sanitize 결과가 원본과 다릅니다.
            [original]
            {}
            ===================================================================
            [sanitized]
            {}
        """, section.getId(), content, html.getContent());
    }
    NodeContent nodeContent = section.getContent();
    nodeContent.updateContent(html);
    return Result.success(SectionDtoWithContent.from(section));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: file 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: 파일 업로드에 실패한 경우
   * @code 그 외 FileValidator에 따라서 다양한 ResultCode 발생 가능
   */
  public Result<SectionAttachmentUrl> uploadAttachmentImage(NodeIdentifier identifier, MultipartFile file) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    // editable한 node인지 검사할 겸 조회.
    TocSection section = editTocPort.loadEditableSection(book, identifier.getNodeId()).get();
    // 권한 검사 ==============================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result checkWriteAuthorityRes = accessGranter.grant(BookAccess.WRITE);
    if(checkWriteAuthorityRes.isFailure()) {
      return checkWriteAuthorityRes;
    }
    // 파일 업로드 ============================================
    Result<FileUploadCmd> cmd = FileUploadCmd.createCmd(
      file,
      section.getId().toString(),
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
