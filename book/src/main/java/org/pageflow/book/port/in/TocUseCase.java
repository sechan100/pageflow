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
import org.pageflow.book.port.in.cmd.NodeAccessIds;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
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
  private final BookPersistencePort bookPersistencePort;
  private final FolderPersistencePort folderPersistencePort;
  private final SectionPersistencePort sectionPersistencePort;
  private final NodePersistencePort nodePersistencePort;


  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code DATA_NOT_FOUND: target, destFolder를 찾을 수 없는 경우
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우 등
   */
  public Result relocateNode(RelocateNodeCmd cmd) {
    UID uid = cmd.getUid();
    // 책 쓰기 권한 확인 ========
    Book book = bookPersistencePort.findById(cmd.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }
    // ==================

    Optional<TocNode> targetOpt = nodePersistencePort.findById(cmd.getNodeId().getValue());
    if(targetOpt.isEmpty()) {
      return Result.of(
        CommonCode.DATA_NOT_FOUND,
        "target node를 찾을 수 없습니다."
      );
    }
    TocNode target = targetOpt.get();
    if(target.isRootFolder()) {
      return Result.of(
        BookCode.TOC_HIERARCHY_ERROR,
        "root folder node는 이동할 수 없습니다."
      );
    }

    UUID destFolderId = cmd.getDestFolderId().getValue();
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
      List<TocNode> allBookNodes = nodePersistencePort.findAllByBookId(cmd.getBookId().getValue());
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
    UUID parentId = cmd.getParentNodeId().getValue();
    Result<NodeTitle> titleRes = NodeTitle.create(cmd.getTitle());
    if(titleRes.isFailure()) {
      return (Result) titleRes;
    }
    NodeTitle title = titleRes.getSuccessData();

    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(cmd.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // Folder 생성 ===================
    Book bookProxy = bookPersistencePort.getReferenceById(cmd.getBookId().getValue());
    Folder folder = Folder.create(
      bookProxy,
      title,
      null,
      0
    );

    // Toc 삽입 ======================
    LastIndexInserter inserter = new LastIndexInserter(cmd.getBookId().getValue(), parentId, nodePersistencePort);
    inserter.insertLast(folder);
    Folder persisted = folderPersistencePort.persist(folder);
    return Result.success(FolderDto.from(persisted));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result<FolderDto> getFolder(NodeAccessIds ids) {
    UID uid = ids.getUid();
    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(ids.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    TocNode node = nodePersistencePort.findById(ids.getNodeId().getValue()).get();
    return Result.success(FolderDto.from(node));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<FolderDto> changeFolderTitle(NodeAccessIds ids, String title) {
    UID uid = ids.getUid();
    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(ids.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    Folder folder = folderPersistencePort.findById(ids.getNodeId().getValue()).get();
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
  public Result deleteFolder(NodeAccessIds ids) {
    UID uid = ids.getUid();
    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(ids.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    nodePersistencePort.deleteById(ids.getNodeId().getValue());
    return Result.success();
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<SectionDtoWithContent> createSection(CreateSectionCmd cmd) {
    UUID bookId = cmd.getBookId().getValue();
    UID uid = cmd.getUid();
    UUID parentId = cmd.getParentNodeId().getValue();
    Result<NodeTitle> newTitleRes = NodeTitle.create(cmd.getTitle());
    if(newTitleRes.isFailure()) {
      return (Result) newTitleRes;
    }
    NodeTitle title = newTitleRes.getSuccessData();

    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(bookId).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // Section 생성 ===========
    Book bookProxy = bookPersistencePort.getReferenceById(bookId);
    Folder parentFolder = folderPersistencePort.findById(parentId).orElseThrow();
    Section section = Section.create(
      bookProxy,
      title,
      parentFolder,
      0
    );

    // Toc 삽입 ============
    LastIndexInserter inserter = new LastIndexInserter(bookId, parentId, nodePersistencePort);
    inserter.insertLast(section);
    Section persisted = sectionPersistencePort.persist(section);
    return Result.success(SectionDtoWithContent.from(persisted));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   */
  public Result<SectionDto> getSection(NodeAccessIds ids) {
    UID uid = ids.getUid();
    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(ids.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    TocNode node = nodePersistencePort.findById(ids.getNodeId().getValue()).get();
    return Result.success(SectionDto.from(node));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 작가 권한이 없는 경우
   * @code INVALID_BOOK_STATUS: 출판된 책을 수정하려는 경우
   * @code FIELD_VALIDATION_ERROR: title이 유효하지 않은 경우
   */
  public Result<SectionDto> changeSectionTitle(NodeAccessIds ids, String title) {
    UID uid = ids.getUid();
    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(ids.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    Section section = sectionPersistencePort.findById(ids.getNodeId().getValue()).get();
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
  public Result deleteSection(NodeAccessIds ids) {
    UID uid = ids.getUid();
    // 책 쓰기 권한 확인 ==============
    Book book = bookPersistencePort.findById(ids.getBookId().getValue()).get();
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    nodePersistencePort.deleteById(ids.getNodeId().getValue());
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

  private Optional<Integer> loadMaxOvAmongSiblings(UUID bookId, UUID parentNodeId) {
    return nodePersistencePort.findMaxOvAmongSiblings(bookId, parentNodeId);
  }
}
