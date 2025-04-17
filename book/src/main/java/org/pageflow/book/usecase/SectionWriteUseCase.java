package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.node.SectionAttachmentUrl;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.SectionHtmlContent;
import org.pageflow.book.domain.toc.entity.SectionContent;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.LoadEditableTocNodePort;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
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
  private final LoadEditableTocNodePort loadEditableTocNodePort;
  private final FileService fileService;

  /**
   * 작가 권한으로 section을 읽어온다.
   * 독자의 section read를 위해서는 다른 메소드를 사용.
   *
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result<WithContentSectionDto> getSectionWithContent(NodeIdentifier identifier) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    Result<TocSection> loadSectionResult = loadEditableTocNodePort.loadEditableSection(book, identifier.getNodeId());
    if(loadSectionResult.isFailure()) {
      return (Result) loadSectionResult;
    }
    TocSection section = loadSectionResult.get();
    // 권한 검사 =========================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result checkWriteAuthorityRes = accessGranter.grant(BookAccess.WRITE);
    if(checkWriteAuthorityRes.isFailure()) {
      return checkWriteAuthorityRes;
    }
    return Result.ok(WithContentSectionDto.from(section));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code DATA_NOT_FOUND: 섹션을 찾을 수 없는 경우
   */
  public Result<WithContentSectionDto> writeContent(NodeIdentifier identifier, String contentStr) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    Result<TocSection> loadSectionResult = loadEditableTocNodePort.loadEditableSection(book, identifier.getNodeId());
    if(loadSectionResult.isFailure()) {
      return (Result) loadSectionResult;
    }
    TocSection section = loadSectionResult.get();
    // 권한 검사 ==================================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result checkWriteAuthorityRes = accessGranter.grant(BookAccess.WRITE);
    if(checkWriteAuthorityRes.isFailure()) return checkWriteAuthorityRes;
    // 내용 작성 ===================================================
    SectionHtmlContent html = new SectionHtmlContent(contentStr);
    if(!html.getIsSanitizationConsistent()) {
      log.warn("""
            Section({})의 content의 html sanitize 결과가 원본과 다릅니다.
            [original]
            {}
            ===================================================================
            [sanitized]
            {}
        """, section.getId(), contentStr, html.getContent());
    }
    int charCount = html.getCharCount();
    SectionContent content = section.getContent();
    content.updateContent(html);
    return Result.ok(WithContentSectionDto.from(section));
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
    Result<TocSection> loadSectionResult = loadEditableTocNodePort.loadEditableSection(book, identifier.getNodeId());
    if(loadSectionResult.isFailure()) {
      return (Result) loadSectionResult;
    }
    TocSection section = loadSectionResult.get();
    // 권한 검사 ==============================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result checkWriteAuthorityRes = accessGranter.grant(BookAccess.WRITE);
    if(checkWriteAuthorityRes.isFailure()) {
      return checkWriteAuthorityRes;
    }
    // 파일 업로드 ============================================
    Result<FileUploadCmd> cmd = FileUploadCmd.createCmd(
      file,
      section.getContent().getId().toString(),
      FileType.BOOK_NODE_CONTENT_ATTACHMENT_IMAGE
    );
    if(cmd.isFailure()) {
      return (Result) cmd;
    }
    Result<FilePath> uploadResult = fileService.upload(cmd.get());
    if(uploadResult.isFailure()) {
      return (Result) uploadResult;
    }
    SectionAttachmentUrl attachmentUrl = new SectionAttachmentUrl(uploadResult.get().getWebUrl());
    return Result.ok(attachmentUrl);
  }

}
