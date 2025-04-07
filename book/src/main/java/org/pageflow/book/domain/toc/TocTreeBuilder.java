package org.pageflow.book.domain.toc;

import org.pageflow.book.application.dto.TocDto;
import org.pageflow.book.domain.entity.Folder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
public class TocTreeBuilder {
//  private final Collection<NodeProjection> nodes;

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
