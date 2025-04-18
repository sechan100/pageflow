package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.node.SectionAttachmentUrl;
import org.pageflow.book.application.dto.node.SectionDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.application.service.GrantedBookLoader;
import org.pageflow.book.application.service.TocNodeLoader;
import org.pageflow.book.application.service.TocNodeLoaderFactory;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.EditableSection;
import org.pageflow.book.domain.toc.SectionHtmlContent;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
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
public class TocSectionUseCase {
  private final GrantedBookLoader grantedBookLoader;
  private final TocNodeLoaderFactory tocNodeLoaderFactory;
  private final FileService fileService;

  /**
   * 작가 권한으로 section을 읽어온다.
   * 독자의 section read를 위해서는 다른 메소드를 사용.
   *
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public WithContentSectionDto getSectionWithContent(NodeIdentifier identifier) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findWithContentById(identifier.getNodeId()));
    return WithContentSectionDto.from(section);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code DATA_NOT_FOUND: 섹션을 찾을 수 없는 경우
   */
  public WithContentSectionDto writeContent(NodeIdentifier identifier, String contentStr) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findWithContentById(identifier.getNodeId()));
    EditableSection editableSection = new EditableSection(section);
    // 내용 작성
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
    editableSection.updateContent(html);
    return WithContentSectionDto.from(section);
  }


  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: file 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: 파일 업로드에 실패한 경우
   * @code 그 외 FileValidator에 따라서 다양한 ResultCode 발생 가능
   */
  public SectionAttachmentUrl uploadAttachmentImage(NodeIdentifier identifier, MultipartFile file) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findWithContentById(identifier.getNodeId()));
    // 파일 업로드
    FileUploadCmd cmd = FileUploadCmd.createCmd(
      file,
      section.getId().toString(),
      FileType.TOC_SECTION_CONTENT_ATTACHMENT_IMAGE
    );
    FilePath filePath = fileService.upload(cmd);
    return new SectionAttachmentUrl(filePath.getWebUrl());
  }

  public SectionDto changeShouldShowTitle(NodeIdentifier identifier, boolean shouldShowTitle) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findWithDetailsById(identifier.getNodeId()));
    EditableSection editableSection = new EditableSection(section);
    editableSection.changeShouldShowTitle(shouldShowTitle);
    return SectionDto.from(section);
  }

  public SectionDto changeShouldBreakSection(NodeIdentifier identifier, boolean shouldBreakSection) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findWithDetailsById(identifier.getNodeId()));
    EditableSection editableSection = new EditableSection(section);
    editableSection.changeShouldBreakSection(shouldBreakSection);
    return SectionDto.from(section);
  }
}
