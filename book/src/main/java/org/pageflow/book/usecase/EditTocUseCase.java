package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.SectionDto;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.application.service.GrantedBookLoader;
import org.pageflow.book.application.service.TocNodeLoader;
import org.pageflow.book.application.service.TocNodeLoaderFactory;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.*;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.persistence.toc.TocNodeRepository;
import org.pageflow.book.persistence.toc.TocRepository;
import org.pageflow.book.usecase.cmd.CreateFolderCmd;
import org.pageflow.book.usecase.cmd.CreateSectionCmd;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.book.usecase.cmd.RelocateNodeCmd;
import org.pageflow.common.result.Ensure;
import org.pageflow.common.result.ResultException;
import org.pageflow.common.user.UID;
import org.pageflow.file.service.FileService;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EditTocUseCase {
  private final GrantedBookLoader grantedBookLoader;
  private final TocNodeLoaderFactory tocNodeLoaderFactory;
  private final TocRepository tocRepository;
  private final FileService fileService;
  private final TocNodeRepository tocNodeRepository;


  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code DATA_NOT_FOUND: destNode를 찾을 수 없는 경우
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우 등
   */
  public void relocateNode(RelocateNodeCmd cmd) {
    Book book = grantedBookLoader.loadBookWithGrant(cmd.getUid(), cmd.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocNode target = nodeLoader.loadNode(repo -> repo.findById(cmd.getNodeId()));
    TocFolder destFolder = nodeLoader.loadFolder(repo -> repo.findWithChildrenById(cmd.getDestFolderId()));

    ParentFolder parent = new ParentFolder(destFolder);
    if(target.isRootFolder()) {
      throw new ResultException(BookCode.TOC_HIERARCHY_ERROR, "root folder는 이동할 수 없습니다.");
    }
    // Reorder
    if(target.getParentNodeOrNull().equals(destFolder)) {
      parent.reorder(cmd.getDestIndex(), target);
    }
    // Reparent
    else {
      parent.reparent(cmd.getDestIndex(), target);
    }
  }

  public TocDto getToc(UID uid, UUID bookId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.WRITE);
    Toc toc = tocRepository.loadEditableToc(book);
    return TocDto.from(toc);
  }

  // ==================================================
  // Folder
  // ==================================================

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public FolderDto createFolder(CreateFolderCmd cmd) {
    Book book = grantedBookLoader.loadBookWithGrant(cmd.getUid(), cmd.getBookId(), BookAccess.WRITE);
    NodeTitle title = NodeTitle.create(cmd.getTitle());
    TocFolder newFolder = TocFolder.create(book, title, cmd.getNodeId());
    newFolder = tocNodeRepository.save(newFolder);

    TocFolder folder = tocNodeLoaderFactory
      .createLoader(book)
      .loadFolder(repo -> repo.findWithChildrenById(cmd.getParentNodeId()));
    ParentFolder parent = new ParentFolder(folder);
    parent.insertLast(newFolder);
    return FolderDto.from(newFolder);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public FolderDto getFolder(NodeIdentifier identifier) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocFolder folder = nodeLoader.loadFolder(repo -> repo.findById(identifier.getNodeId()));
    return FolderDto.from(folder);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public FolderDto changeFolderTitle(NodeIdentifier identifier, String title) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocFolder folder = nodeLoader.loadFolder(repo -> repo.findById(identifier.getNodeId()));
    EditableFolder editableFolder = new EditableFolder(folder);
    NodeTitle newTitle = NodeTitle.create(title);
    editableFolder.changeTitle(newTitle);
    return FolderDto.from(folder);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public void deleteFolder(NodeIdentifier identifier) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocFolder target = nodeLoader.loadFolder(repo -> repo.findById(identifier.getNodeId()));
    Ensure.that(
      !target.isRootFolder(),
      BookCode.TOC_HIERARCHY_ERROR,
      "root folder는 삭제할 수 없습니다."
    );
    TocFolder parent = target.getParentNodeOrNull();
    parent.removeChild(target);
    tocNodeRepository.delete(target);
  }


  // ==================================================
  // Section
  // ==================================================

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public WithContentSectionDto createSection(CreateSectionCmd cmd) {
    Book book = grantedBookLoader.loadBookWithGrant(cmd.getUid(), cmd.getBookId(), BookAccess.WRITE);
    // Section 생성
    NodeTitle title = NodeTitle.create(cmd.getTitle());
    TocSection newSection = TocSection.create(book, title, cmd.getNodeId());
    newSection = tocNodeRepository.save(newSection);
    // 부모 folder에 삽입
    TocFolder folder = tocNodeLoaderFactory
      .createLoader(book)
      .loadFolder(repo -> repo.findWithChildrenById(cmd.getParentNodeId()));
    ParentFolder parent = new ParentFolder(folder);
    parent.insertLast(newSection);
    return WithContentSectionDto.from(newSection);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public SectionDto getSection(NodeIdentifier identifier) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findById(identifier.getNodeId()));
    return SectionDto.from(section);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public SectionDto changeSectionTitle(NodeIdentifier identifier, String title) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findById(identifier.getNodeId()));
    EditableSection editableSection = new EditableSection(section);
    NodeTitle newTitle = NodeTitle.create(title);
    editableSection.changeTitle(newTitle);
    return SectionDto.from(section);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public void deleteSection(NodeIdentifier identifier) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection target = nodeLoader.loadSection(repo -> repo.findById(identifier.getNodeId()));
    // 첨부파일 삭제
    fileService.deleteAll(
      target.getId().toString(),
      FileType.TOC_SECTION_CONTENT_ATTACHMENT_IMAGE
    );
    // 노드 삭제
    TocFolder parent = target.getParentNodeOrNull();
    parent.removeChild(target);
    tocNodeRepository.delete(target);
  }

}

