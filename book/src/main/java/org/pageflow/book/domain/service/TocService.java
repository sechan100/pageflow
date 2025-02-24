package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.BookPermissionRequired;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.LastIndexInserter;
import org.pageflow.book.domain.toc.NodeProjection;
import org.pageflow.book.domain.toc.NodeReplacer;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.*;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.FolderPersistencePort;
import org.pageflow.book.port.out.jpa.NodePersistencePort;
import org.pageflow.book.port.out.jpa.SectionPersistencePort;
import org.pageflow.common.result.AdditionalMessage;
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
public class TocService implements TocUseCase, NodeCrudUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final FolderPersistencePort folderPersistencePort;
  private final SectionPersistencePort sectionPersistencePort;
  private final NodePersistencePort nodePersistencePort;


  @Override
  @BookPermissionRequired
  public void replaceNode(BookPermission permission, ReplaceNodeCmd cmd) {
    TocNode node = nodePersistencePort.findById(cmd.getNodeId()).orElseThrow();
    if(node.getParentNode() == null){
      throw new ProcessResultException(Result.of(
        BookCode.TOC_HIERARCHY_VIOLATION, AdditionalMessage.of("root folder node는 이동할 수 없습니다.")
      ));
    }

    UUID destFolderId = cmd.getDestFolderId();
    Folder folderProxy = folderPersistencePort.getReferenceById(destFolderId);
    List<TocNode> siblings = nodePersistencePort.findChildrenByParentNode_IdOrderByOv(destFolderId);
    NodeReplacer replacer = new NodeReplacer(folderProxy, siblings);
    // Reorder
    if(node.getParentNode().getId().equals(destFolderId)){
      replacer.reorder(cmd.getDestIndex(), node);
    // Reparent
    } else {
      replacer.reparent(cmd.getDestIndex(), node);
    }
  }

  @Override
  @BookPermissionRequired
  public TocDto.Toc loadToc(BookPermission permission) {
    UUID bookId = permission.getBookId();
    List<NodeProjection> nodeProjections = nodePersistencePort.queryNodesByBookId(bookId);
    TocDto.Folder root = buildTree(bookId, nodeProjections);
    return new TocDto.Toc(bookId, root);
  }

  @Override
  @BookPermissionRequired
  public FolderDto createFolder(BookPermission permission, CreateFolderCmd cmd) {
    UUID bookId = cmd.getBookId();
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
  @BookPermissionRequired
  public FolderDto queryFolder(BookPermission permission, UUID folderId) {
    TocNode node = nodePersistencePort.findById(folderId).get();
    return FolderDto.from(node);
  }

  @Override
  @BookPermissionRequired
  public FolderDto updateFolder(BookPermission permission, UpdateFolderCmd cmd) {
    Folder folder = folderPersistencePort.findById(cmd.getId()).get();
    folder.changeTitle(cmd.getTitle().getValue());
    return FolderDto.from(folder);
  }

  @Override
  @BookPermissionRequired
  public void deleteFolder(BookPermission permission, UUID folderId) {
    this.deleteNode(folderId);
  }

  private void deleteNode(UUID nodeId) {
    nodePersistencePort.deleteById(nodeId);
  }

  @Override
  @BookPermissionRequired
  public SectionDtoWithContent createSection(BookPermission permission, CreateSectionCmd cmd) {
    UUID bookId = cmd.getBookId();
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
  @BookPermissionRequired
  public SectionDto querySection(BookPermission permission, UUID sectionId) {
    TocNode node = nodePersistencePort.findById(sectionId).get();
    return SectionDto.from(node);
  }

  @Override
  @BookPermissionRequired
  public SectionDtoWithContent querySectionWithContent(BookPermission permission, UUID sectionId) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    return SectionDtoWithContent.from(section);
  }

  @Override
  @BookPermissionRequired
  public SectionDtoWithContent updateSection(BookPermission permission, UpdateSectionCmd cmd) {
    Section section = sectionPersistencePort.findById(cmd.getId()).get();
    section.changeTitle(cmd.getTitle().getValue());
    section.updateContent(cmd.getContent());
    return SectionDtoWithContent.from(section);
  }

  @Override
  @BookPermissionRequired
  public void deleteSection(BookPermission permission, UUID sectionId) {
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

  private Optional<Integer> loadMaxOvAmongSiblings(UUID bookId, UUID parentNodeId) {
    return nodePersistencePort.findMaxOvAmongSiblings(bookId, parentNodeId);
  }
}
