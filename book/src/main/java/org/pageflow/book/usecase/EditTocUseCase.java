package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.SectionDto;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.NodeTitle;
import org.pageflow.book.domain.toc.ParentFolder;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.LoadEditableTocNodePort;
import org.pageflow.book.persistence.toc.TocPersistencePort;
import org.pageflow.book.usecase.cmd.CreateFolderCmd;
import org.pageflow.book.usecase.cmd.CreateSectionCmd;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.book.usecase.cmd.RelocateNodeCmd;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
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
  private final BookPersistencePort bookPersistencePort;
  private final TocPersistencePort tocPersistencePort;
  private final LoadEditableTocNodePort loadEditableTocNodePort;
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
    Result<TocNode> loadTargetResult = loadEditableTocNodePort.loadEditableNode(book, cmd.getNodeId());
    if(loadTargetResult.isFailure()) {
      return loadTargetResult;
    }
    TocNode target = loadTargetResult.get();
    if(target.isRootFolder()) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, "root folder는 이동할 수 없습니다.");
    }
    UUID destFolderId = cmd.getDestFolderId();
    Result<TocFolder> loadFolderResult = loadEditableTocNodePort.loadEditableFolder(book, destFolderId);
    if(loadFolderResult.isFailure()) {
      return loadFolderResult;
    }
    ParentFolder folder = new ParentFolder(loadFolderResult.get());
    // Reorder
    if(target.getParentNodeOrNull().getId().equals(destFolderId)) {
      return folder.reorder(cmd.getDestIndex(), target);
    }
    // Reparent
    else {
      return folder.reparent(cmd.getDestIndex(), target);
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
    Toc toc = tocPersistencePort.loadEditableToc(book);
    return Result.SUCCESS(TocDto.from(toc));
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
    // 책 쓰기 권한 확인
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
    NodeTitle title = titleRes.get();
    TocFolder newFolder = TocFolder.create(book, title);
    // Toc 삽입
    Result<TocFolder> loadParentResult = loadEditableTocNodePort.loadEditableFolder(book, cmd.getParentNodeId());
    if(loadParentResult.isFailure()) {
      return (Result) loadParentResult;
    }
    ParentFolder parent = new ParentFolder(loadParentResult.get());
    parent.insertLast(newFolder);
    return Result.SUCCESS(FolderDto.from(newFolder));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result<FolderDto> getFolder(NodeIdentifier identifier) {
    Result<TocNode> nodeRes = _loadAfterGrantWriteAccess(identifier);
    if(nodeRes.isFailure()) {
      return (Result) nodeRes;
    }
    TocNode folder = nodeRes.get();
    if(folder instanceof TocFolder f) {
      return Result.SUCCESS(FolderDto.from((TocFolder) folder));
    } else {
      return Result.of(CommonCode.DATA_NOT_FOUND, "해당 노드는 폴더가 아닙니다.");
    }
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
    TocNode folder = nodeRes.get();
    if(folder instanceof TocFolder f) {
      return Result.SUCCESS(FolderDto.from((TocFolder) folder));
    } else {
      return Result.of(CommonCode.DATA_NOT_FOUND, "해당 노드는 폴더가 아닙니다.");
    }
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result deleteFolder(NodeIdentifier identifier) {
    Result<TocNode> nodeRes = _loadAfterGrantWriteAccess(identifier);
    if(nodeRes.isFailure()) {
      return nodeRes;
    }
    TocNode target = nodeRes.get();
    // 노드 삭제 ==================================================
    if(target.isRootFolder()) {
      return Result.of(
        BookCode.TOC_HIERARCHY_ERROR,
        "root folder는 삭제할 수 없습니다."
      );
    }
    TocFolder parent = target.getParentNodeOrNull();
    parent.removeChild(target);
    return Result.SUCCESS();
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
    // 책 쓰기 권한 확인
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    // Section 생성
    Result<NodeTitle> newTitleRes = NodeTitle.create(cmd.getTitle());
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }
    NodeTitle title = newTitleRes.get();
    TocSection newSection = TocSection.create(book, title);
    // Toc 삽입
    Result<TocFolder> loadFolderResult = loadEditableTocNodePort.loadEditableFolder(book, cmd.getParentNodeId());
    if(loadFolderResult.isFailure()) {
      return (Result) loadFolderResult;
    }
    ParentFolder parent = new ParentFolder(loadFolderResult.get());
    parent.insertLast(newSection);
    return Result.SUCCESS(WithContentSectionDto.from(newSection));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result<SectionDto> getSection(NodeIdentifier identifier) {
    Result<TocNode> nodeRes = _loadAfterGrantWriteAccess(identifier);
    if(nodeRes.isFailure()) {
      return (Result) nodeRes;
    }
    TocNode section = nodeRes.get();
    if(section instanceof TocSection s) {
      return Result.SUCCESS(SectionDto.from(s));
    } else {
      return Result.of(CommonCode.DATA_NOT_FOUND, "해당 노드는 섹션이 아닙니다.");
    }
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
    TocNode section = changeResult.get();
    if(section instanceof TocSection s) {
      return Result.SUCCESS(SectionDto.from(s));
    } else {
      return Result.of(CommonCode.DATA_NOT_FOUND, "해당 노드는 섹션이 아닙니다.");
    }
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   */
  public Result deleteSection(NodeIdentifier identifier) {
    Result<TocNode> nodeRes = _loadAfterGrantWriteAccess(identifier);
    if(nodeRes.isFailure()) {
      return nodeRes;
    }
    if(!(nodeRes.get() instanceof TocSection)) {
      throw new IllegalArgumentException("해당 노드는 섹션이 아닙니다.");
    }
    TocSection target = (TocSection) nodeRes.get();
    if(target.isRootFolder()) {
      return Result.of(
        BookCode.TOC_HIERARCHY_ERROR,
        "root folder node는 삭제할 수 없습니다."
      );
    }
    // 첨부파일 삭제
    String contentId = target.getContent().getId().toString();
    Result contentAttachedFileDeletionResult = fileService.deleteAll(contentId, FileType.BOOK_NODE_CONTENT_ATTACHMENT_IMAGE);
    if(contentAttachedFileDeletionResult.isFailure()) {
      return contentAttachedFileDeletionResult;
    }
    // 노드 삭제
    TocFolder parent = target.getParentNodeOrNull();
    parent.removeChild(target);
    return Result.SUCCESS();
  }

  private Result<TocNode> _loadAfterGrantWriteAccess(NodeIdentifier identifier) {
    Book book = bookPersistencePort.findById(identifier.getBookId()).get();
    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(identifier.getUid(), book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    Result<TocNode> loadNodeResult = loadEditableTocNodePort.loadEditableNode(book, identifier.getNodeId());
    return loadNodeResult;
  }

  private Result<TocNode> _changeTitle(NodeIdentifier identifier, String title) {
    Result<TocNode> nodeResult = _loadAfterGrantWriteAccess(identifier);
    if(nodeResult.isFailure()) {
      return nodeResult;
    }
    TocNode node = nodeResult.get();
    Result<NodeTitle> newTitleRes = NodeTitle.create(title);
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }
    node.changeTitle(newTitleRes.get());
    return Result.SUCCESS(node);
  }
}

