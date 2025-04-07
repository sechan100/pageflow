package org.pageflow.book.domain.toc;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.pageflow.book.application.TocNodeType;
import org.pageflow.book.domain.entity.TocNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 트리 구조를 만들기 위한 노드 클래스
 *
 * @author : sechan
 */
@Getter
public class TreeNode {
  private final UUID id;
  private final UUID parentId; // root 노드인 경우 null
  private final int ov;
  private final TocNodeType type;
  private final List<TreeNode> children = new ArrayList<>();

  public TreeNode(TocNode node) {
    this.id = node.getId();
    this.parentId = node.isRootFolder() ? null : node.getParentNodeOrNull().getId();
    this.ov = node.getOv();
    this.type = node.getType();
  }

  public void addChildAccordingToOv(TreeNode node) {
    Preconditions.checkState(isFolder());
    Preconditions.checkState(!node.isRoot());

    for(int i = 0; i < children.size(); i++) {
      TreeNode ithChild = children.get(i);
      if(ithChild.equals(node)) {
        throw new IllegalArgumentException("해당 자식 노드가 이미 이 folder의 자식입니다.");
      }
      if(ithChild.ov > node.ov) {
        children.add(i, node);
        return;
      }
    }
    children.add(node);
  }

  public boolean isRoot() {
    return parentId == null;
  }

  public boolean isFolder() {
    return type == TocNodeType.FOLDER;
  }

  public boolean isSection() {
    return type == TocNodeType.SECTION;
  }
}
