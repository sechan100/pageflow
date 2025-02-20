package org.pageflow.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.NodeProjection;
import org.pageflow.book.domain.toc.NodeReplaceCmd;
import org.pageflow.book.domain.toc.NodeReplacer;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.out.persistence.FolderRepository;
import org.pageflow.book.port.out.persistence.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TocService {
  private final NodeRepository nodeRepo;
  private final FolderRepository folderRepo;



  public void replaceNode(NodeReplaceCmd cmd) {
    TocNode node = nodeRepo.findById(cmd.getNodeId()).orElseThrow();
    Assert.notNull(node.getParentNode(), "Root Folder는 이동할 수 없습니다.");

    UUID destFolderId = cmd.getDestfolderId();
    Folder folderProxy = folderRepo.getReferenceById(destFolderId);
    List<TocNode> siblings = nodeRepo.findChildrenByParentNode_IdOrderByOv(destFolderId);
    NodeReplacer replacer = new NodeReplacer(folderProxy, siblings);
    // Reorder
    if(node.getParentNode().getId().equals(destFolderId)){
      replacer.reorder(cmd.getDestIndex(), node);
    // Reparent
    } else {
      replacer.reparent(cmd.getDestIndex(), node);
    }
  }

  public TocDto.Toc loadToc(UUID bookId) {
    List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId);
    TocDto.Folder root = buildTree(bookId, nodeProjections);
    return new TocDto.Toc(bookId, root);
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
    if(rootProjection==null) throw new IllegalStateException("Root Folder가 없습니다.");
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
    return nodeRepo.findMaxOvAmongSiblings(bookId, parentNodeId);
  }
}
