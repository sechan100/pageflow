package org.pageflow.book.port.in;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.SectionDto;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.TocFolder;
import org.pageflow.book.domain.toc.TocSection;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.NodeIdentifier;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.book.port.out.EditTocPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.pageflow.file.service.FileService;
import org.pageflow.file.shared.FileType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EditTocUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final EditTocPort editTocPort;
  private final FileService fileService;


  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code DATA_NOT_FOUND: destNode를 찾을 수 없는 경우
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우 등
   */
  public Result relocateNode(RelocateNodeCmd cmd) {
    UID uid = cmd.getUid();
    Book book = bookPersistencePort.findById(cmd.getBookId()).get();
    // 책에 대한 쓰기 권한 확인 ==================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    // 노드 이동 ======================
    TocNode target = editTocPort.loadEditableNode(book, cmd.getNodeId()).get();
    if(target.isRootFolder()) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, "root folder는 이동할 수 없습니다.");
    }
    UUID destFolderId = cmd.getDestFolderId();
    Optional<TocFolder> destFolderOpt = editTocPort.loadEditableFolder(book, destFolderId);
    if(destFolderOpt.isEmpty()) {
      return Result.of(
        CommonCode.DATA_NOT_FOUND,
        "dest folder를 찾을 수 없습니다."
      );
    }
    TocFolder folder = destFolderOpt.get();
    // Reorder
    if(target.getParentNodeOrNull().getId().equals(destFolderId)) {
      return folder.reorder(cmd.getDestIndex(), target);
    }
    // Reparent
    else {
      Toc toc = editTocPort.loadEditableToc(book);
      return folder.reparent(cmd.getDestIndex(), target, toc);
    }
  }

  public Result<TocDto> getToc(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    // 책에 대한 쓰기 권한 확인 ==================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    Toc toc = editTocPort.loadEditableToc(book);
    return Result.success(toc.buildTreeDto());
  }

  // ==================================================
  // Folder
  // ==================================================

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<FolderDto> createFolder(CreateFolderCmd cmd) {
    UID uid = cmd.getUid();
    Book book = bookPersistencePort.findById(cmd.getBookId()).get();
    TocFolder parent = editTocPort.loadEditableFolder(book, cmd.getParentNodeId()).get();
    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    // Folder 생성 ===================
    Result<NodeTitle> titleRes = NodeTitle.create(cmd.getTitle());
    if(titleRes.isFailure()) {
      return (Result) titleRes;
    }
    NodeTitle title = titleRes.getSuccessData();
    TocNode folder = TocNode.createFolder(book, title);
    // Toc 삽입
    parent.insertLast(folder);
    TocNode persisted = editTocPort.persist(folder);
    return Result.success(FolderDto.from(persisted));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result<FolderDto> getFolder(NodeIdentifier identifier) {
    Result<TocNode> nodeRes = _loadNode(identifier);
    if(nodeRes.isFailure()) {
      return (Result) nodeRes;
    }
    TocNode folder = nodeRes.getSuccessData();
    Preconditions.checkState(folder.isFolderType());
    return Result.success(FolderDto.from(nodeRes.getSuccessData()));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<FolderDto> changeFolderTitle(NodeIdentifier identifier, String title) {
    Result<TocNode> nodeRes = _changeTitle(identifier, title);
    if(nodeRes.isFailure()) {
      return (Result) nodeRes;
    }
    return Result.success(FolderDto.from(nodeRes.getSuccessData()));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result deleteFolder(NodeIdentifier identifier) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    // 조회해서 folder가 book에 속하는지 확인
    TocNode folder = editTocPort.loadEditableNode(book, identifier.getNodeId()).get();
    // 책 쓰기 권한 확인 ===============================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    // 노드 삭제 ==================================================
    if(folder.isRootFolder()) {
      return Result.of(
        BookCode.TOC_HIERARCHY_ERROR,
        "root folder node는 삭제할 수 없습니다."
      );
    }
    Toc toc = editTocPort.loadEditableToc(book);
    editTocPort.deleteFolder(toc, folder.getId());
    return Result.success();
  }


  // ==================================================
  // Section
  // ==================================================

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<WithContentSectionDto> createSection(CreateSectionCmd cmd) {
    UID uid = cmd.getUid();
    Book book = bookPersistencePort.findById(cmd.getBookId()).get();
    TocFolder parentFolder = editTocPort.loadEditableFolder(book, cmd.getParentNodeId()).get();
    // 책 쓰기 권한 확인 ==================================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    // Section 생성 ============================
    Result<NodeTitle> newTitleRes = NodeTitle.create(cmd.getTitle());
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }
    NodeTitle title = newTitleRes.getSuccessData();
    TocNode section = TocNode.createSection(
      book,
      title
    );
    // Toc 삽입
    parentFolder.insertLast(section);
    editTocPort.persist(section);
    TocSection tocSection = editTocPort.loadEditableSection(book, section.getId()).get();
    return Result.success(WithContentSectionDto.from(tocSection));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result<SectionDto> getSection(NodeIdentifier identifier) {
    Result<TocNode> nodeRes = _loadNode(identifier);
    if(nodeRes.isFailure()) {
      return (Result) nodeRes;
    }
    TocNode section = nodeRes.getSuccessData();
    Preconditions.checkState(section.isSectionType());
    return Result.success(SectionDto.from(nodeRes.getSuccessData()));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<SectionDto> changeSectionTitle(NodeIdentifier identifier, String title) {
    Result<TocNode> changeResult = _changeTitle(identifier, title);
    if(changeResult.isFailure()) {
      return (Result) changeResult;
    }
    return Result.success(SectionDto.from(changeResult.getSuccessData()));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result deleteSection(NodeIdentifier identifier) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    // 조회해서 section이 book에 속하는지 확인
    TocSection found = editTocPort.loadEditableSection(book, identifier.getNodeId()).get();
    TocNode section = found.getTocNodeEntity();
    // 책 쓰기 권한 확인 ===============================================
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    if(section.isRootFolder()) {
      return Result.of(
        BookCode.TOC_HIERARCHY_ERROR,
        "root folder node는 삭제할 수 없습니다."
      );
    }
    // 첨부파일 삭제
    String contentId = section.getContent().getId().toString();
    Result contentAttachedFileDeletionResult = fileService.deleteAll(contentId, FileType.BOOK_NODE_CONTENT_ATTACHMENT_IMAGE);
    if(contentAttachedFileDeletionResult.isFailure()) {
      return contentAttachedFileDeletionResult;
    }
    // 노드 삭제
    editTocPort.deleteSection(section);
    return Result.success();
  }


  // ==================================================
  // Private Methods
  // ==================================================

  private Result<TocNode> _loadNode(NodeIdentifier identifier) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    TocNode node = editTocPort.loadEditableNode(book, identifier.getNodeId()).get();
    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    return Result.success(node);
  }

  private Result<TocNode> _changeTitle(NodeIdentifier identifier, String title) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    TocNode node = editTocPort.loadEditableNode(book, identifier.getNodeId()).get();
    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    Result<NodeTitle> newTitleRes = NodeTitle.create(title);
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }
    node.changeTitle(newTitleRes.getSuccessData());
    return Result.success(node);
  }
}

