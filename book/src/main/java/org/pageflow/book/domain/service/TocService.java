package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.LastIndexInserter;
import org.pageflow.book.domain.toc.NodeProjection;
import org.pageflow.book.domain.toc.NodeRelocator;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.TocNodeUseCase;
import org.pageflow.book.port.in.TocUseCase;
import org.pageflow.book.port.in.cmd.*;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.FolderPersistencePort;
import org.pageflow.book.port.out.jpa.NodePersistencePort;
import org.pageflow.book.port.out.jpa.SectionPersistencePort;
import org.pageflow.common.permission.PermissionRequired;
import org.pageflow.common.result.MessageData;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.Result;
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
public class TocService implements TocUseCase, TocNodeUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final FolderPersistencePort folderPersistencePort;
  private final SectionPersistencePort sectionPersistencePort;
  private final NodePersistencePort nodePersistencePort;


  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public void relocateNode(@BookId UUID bookId, RelocateNodeCmd cmd) {
    TocNode target = nodePersistencePort.findById(cmd.getNodeId()).orElseThrow();
    if(target.isRootFolder()){
      Result hierarchyViolationResult = Result.of(
        BookCode.TOC_HIERARCHY_VIOLATION,
        MessageData.of("root folder node는 이동할 수 없습니다.")
      );
      throw new ProcessResultException(hierarchyViolationResult);
    }

    UUID destFolderId = cmd.getDestFolderId();
    Folder folderWithChildren = folderPersistencePort.findWithChildrenById(destFolderId);
    NodeRelocator relocator = new NodeRelocator(folderWithChildren);
    // Reorder
    if(target.ensureParentNode().getId().equals(destFolderId)){
      relocator.reorder(cmd.getDestIndex(), target);
    // Reparent
    } else {
      List<TocNode> allBookNodes = nodePersistencePort.findAllByBookId(bookId);
      relocator.reparent(cmd.getDestIndex(), target, allBookNodes);
    }
  }

  @Override
  @PermissionRequired(
    actions = { "READ" },
    permissionType = BookPermission.class
  )
  public TocDto.Toc loadToc(@BookId UUID bookId) {
    List<NodeProjection> nodeProjections = nodePersistencePort.queryNodesByBookId(bookId);
    TocDto.Folder root = buildTree(bookId, nodeProjections);
    return new TocDto.Toc(bookId, root);
  }

  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public FolderDto createFolder(@BookId UUID bookId, CreateFolderCmd cmd) {
    UUID parentId = cmd.getParentNodeId();
    UUID folderId = UUID.randomUUID();

    // Folder 생성
    Book bookProxy = bookPersistencePort.getReferenceById(cmd.getBookId());
    Folder folder = new Folder(
      folderId,
      bookProxy,
      cmd.getTitle().getValue(),
      null,
      0
    );
    // 부모 지정해주고 마지막 순서로 끼워넣기
    LastIndexInserter inserter = new LastIndexInserter(bookId, parentId, nodePersistencePort);
    inserter.insertLast(folder);
    // insert
    Folder persisted = folderPersistencePort.persist(folder);
    return FolderDto.from(persisted);
  }

  @Override
  @PermissionRequired(
    actions = { "READ" },
    permissionType = BookPermission.class
  )
  public FolderDto queryFolder(@BookId UUID bookId, UUID folderId) {
    TocNode node = nodePersistencePort.findById(folderId).get();
    return FolderDto.from(node);
  }

  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public FolderDto updateFolder(@BookId UUID bookId, UpdateFolderCmd cmd) {
    Folder folder = folderPersistencePort.findById(cmd.getId()).get();
    folder.changeTitle(cmd.getTitle().getValue());
    return FolderDto.from(folder);
  }

  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public void deleteFolder(@BookId UUID bookId, UUID folderId) {
    this.deleteNode(folderId);
  }

  private void deleteNode(UUID nodeId) {
    nodePersistencePort.deleteById(nodeId);
  }

  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public SectionDtoWithContent createSection(@BookId UUID bookId, CreateSectionCmd cmd) {
    UUID parentId = cmd.getParentNodeId();
    UUID sectionId = UUID.randomUUID();

    // Section 생성
    Book bookProxy = bookPersistencePort.getReferenceById(cmd.getBookId());
    Folder parentFolder = folderPersistencePort.findById(parentId).orElseThrow();
    Section section = new Section(
      sectionId,
      bookProxy,
      cmd.getTitle().getValue(),
      parentFolder,
      cmd.getContent(),
      0
    );
    // 부모 지정해주고 마지막 순서로 끼워넣기
    LastIndexInserter inserter = new LastIndexInserter(bookId, parentId, nodePersistencePort);
    inserter.insertLast(section);
    // insert
    Section persisted = sectionPersistencePort.persist(section);
    return SectionDtoWithContent.from(persisted);
  }

  @Override
  @PermissionRequired(
    actions = { "READ" },
    permissionType = BookPermission.class
  )
  public SectionDto querySection(@BookId UUID bookId, UUID sectionId) {
    TocNode node = nodePersistencePort.findById(sectionId).get();
    return SectionDto.from(node);
  }

  @Override
  @PermissionRequired(
    actions = { "READ" },
    permissionType = BookPermission.class
  )
  public SectionDtoWithContent querySectionWithContent(@BookId UUID bookId, UUID sectionId) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    return SectionDtoWithContent.from(section);
  }

  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public SectionDtoWithContent updateSection(@BookId UUID bookId, UpdateSectionCmd cmd) {
    Section section = sectionPersistencePort.findById(cmd.getId()).get();
    section.changeTitle(cmd.getTitle().getValue());
    section.updateContent(cmd.getContent());
    return SectionDtoWithContent.from(section);
  }

  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public void deleteSection(@BookId UUID bookId, UUID sectionId) {
    this.deleteNode(sectionId);
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
    for(NodeProjection p : projections){
      UUID parentId = p.getParentId();
      // Root Folder
      if(parentId == null){
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
    if(projection.getType().equals(Folder.class)){
      List<TocDto.Node> children;
      if(projection.getChildren()!=null){
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

  private Optional<Integer> loadMaxOvAmongSiblings(@BookId UUID bookId, UUID parentNodeId) {
    return nodePersistencePort.findMaxOvAmongSiblings(bookId, parentNodeId);
  }
}
