package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.FolderDto;
import org.pageflow.book.application.dto.SectionDto;
import org.pageflow.book.application.dto.SectionDtoWithContent;
import org.pageflow.book.application.dto.TocDto;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.NodeTitle;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.domain.toc.LastIndexInserter;
import org.pageflow.book.domain.toc.NodeProjection;
import org.pageflow.book.domain.toc.NodeRelocator;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.book.port.out.jpa.FolderPersistencePort;
import org.pageflow.book.port.out.jpa.NodePersistencePort;
import org.pageflow.book.port.out.jpa.SectionPersistencePort;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TocUseCase {
  private final FolderPersistencePort folderPersistencePort;
  private final SectionPersistencePort sectionPersistencePort;
  private final NodePersistencePort nodePersistencePort;


  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code DATA_NOT_FOUND: destNode를 찾을 수 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우 등
   */
  public Result relocateNode(RelocateNodeCmd cmd) {
    UID uid = cmd.getUid();
    TocNode target = nodePersistencePort.findById(cmd.getNodeId()).get();
    Book book = target.getBook();

    // 책에 대한 쓰기 권한 확인 ==================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // 노드 검증 및 이동 ======================
    if(target.isRootFolder()) {
      return Result.of(
        BookCode.TOC_HIERARCHY_ERROR,
        "root folder node는 이동할 수 없습니다."
      );
    }
    UUID destFolderId = cmd.getDestFolderId();
    Optional<Folder> folderOpt = folderPersistencePort.findWithChildrenById(destFolderId);
    if(folderOpt.isEmpty()) {
      return Result.of(
        CommonCode.DATA_NOT_FOUND,
        "dest folder를 찾을 수 없습니다."
      );
    }
    Folder folderWithChildren = folderOpt.get();
    NodeRelocator relocator = new NodeRelocator(folderWithChildren);
    if(target.ensureParentNode().getId().equals(destFolderId)) {
      // Reorder
      return relocator.reorder(cmd.getDestIndex(), target);
    } else {
      // Reparent
      List<TocNode> allBookNodes = nodePersistencePort.findAllByBookId(book.getId());
      return relocator.reparent(cmd.getDestIndex(), target, allBookNodes);
    }
  }

  public TocDto.Toc getToc(UUID bookId) {
    List<NodeProjection> nodeProjections = nodePersistencePort.queryNodesByBookId(bookId);
    TocDto.Folder root = buildTree(bookId, nodeProjections);
    return new TocDto.Toc(bookId, root);
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<FolderDto> createFolder(CreateFolderCmd cmd) {
    UID uid = cmd.getUid();
    Folder parentFolder = folderPersistencePort.findById(cmd.getParentNodeId()).get();
    Book book = parentFolder.getBook();
    UUID parentId = cmd.getParentNodeId();
    Result<NodeTitle> titleRes = NodeTitle.create(cmd.getTitle());
    if(titleRes.isFailure()) {
      return (Result) titleRes;
    }
    NodeTitle title = titleRes.getSuccessData();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // Folder 생성 ===================
    Folder folder = Folder.create(
      book,
      title,
      0
    );
    // Toc 삽입
    LastIndexInserter inserter = new LastIndexInserter(parentFolder);
    inserter.insertLast(folder);
    Folder persisted = folderPersistencePort.persist(folder);
    return Result.success(FolderDto.from(persisted));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result<FolderDto> getFolder(UID uid, UUID folderId) {
    Folder folder = folderPersistencePort.findById(folderId).get();
    Book book = folder.getBook();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    return Result.success(FolderDto.from(folder));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<FolderDto> changeFolderTitle(UID uid, UUID folderId, String title) {
    Folder folder = folderPersistencePort.findById(folderId).get();
    Book book = folder.getBook();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    Result<NodeTitle> newTitleRes = NodeTitle.create(title);
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }
    folder.changeTitle(newTitleRes.getSuccessData());
    return Result.success(FolderDto.from(folder));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result deleteFolder(UID uid, UUID folderId) {
    Folder folder = folderPersistencePort.findById(folderId).get();
    Book book = folder.getBook();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    folderPersistencePort.delete(folder);
    return Result.success();
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<SectionDtoWithContent> createSection(CreateSectionCmd cmd) {
    UID uid = cmd.getUid();
    Folder parentFolder = folderPersistencePort.findById(cmd.getParentNodeId()).get();
    Book book = parentFolder.getBook();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // Section 생성 ================
    Result<NodeTitle> newTitleRes = NodeTitle.create(cmd.getTitle());
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }
    NodeTitle title = newTitleRes.getSuccessData();
    Section section = Section.create(
      book,
      title,
      0
    );
    // Toc 삽입
    LastIndexInserter inserter = new LastIndexInserter(parentFolder);
    inserter.insertLast(section);
    Section persisted = sectionPersistencePort.persist(section);
    return Result.success(SectionDtoWithContent.from(persisted));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result<SectionDto> getSection(UID uid, UUID sectionId) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    Book book = section.getBook();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    return Result.success(SectionDto.from(section));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<SectionDto> changeSectionTitle(UID uid, UUID sectionId, String title) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    Book book = section.getBook();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    Result<NodeTitle> newTitleRes = NodeTitle.create(title);
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }

    section.changeTitle(newTitleRes.getSuccessData());
    return Result.success(SectionDto.from(section));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result deleteSection(UID uid, UUID sectionId) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    Book book = section.getBook();

    // 책 쓰기 권한 확인 ==============
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    nodePersistencePort.delete(section);
    return Result.success();
  }


  /**
   * NodeProjection로 트리를 구성하고 TocDto.Node 기반의 트리로 변환하여 그 root를 반환한다.
   */
  private TocDto.Folder buildTree(UUID bookId, List<NodeProjection> projections) {
    // 트리 구성
    Map<UUID, NodeProjection> nodeMap = projections
      .stream()
      .collect(Collectors.toMap(NodeProjection::getId, p -> p));

    NodeProjection rootProjection = null;
    for(NodeProjection p : projections) {
      UUID parentId = p.getParentId();
      // Root Folder
      if(parentId == null) {
        rootProjection = p;
        continue;
      }

      NodeProjection parent = nodeMap.get(parentId);
      parent.addChildAccordingToOv(p);
    }

    // NodeProjection -> Dto로 변환
    if(rootProjection == null) throw new IllegalStateException("Root Folder가 없습니다.");
    List<TocDto.Node> rootChildren = rootProjection.getChildren().stream()
      .map(this::projectRecursively)
      .toList();

    return new TocDto.Folder(
      rootProjection.getId(),
      rootProjection.getTitle(),
      rootChildren
    );
  }

  private TocDto.Node projectRecursively(NodeProjection projection) {
    if(projection.getType().equals(Folder.class)) {
      List<TocDto.Node> children;
      if(projection.getChildren() != null) {
        children = projection.getChildren().stream()
          .map(c -> this.projectRecursively(c))
          .toList();
      } else {
        children = Collections.emptyList();
      }
      return new TocDto.Folder(
        projection.getId(),
        projection.getTitle(),
        children
      );
    } else {
      return new TocDto.Section(
        projection.getId(),
        projection.getTitle()
      );
    }
  }

}
