package org.pageflow.book.domain.toc;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.pageflow.book.domain.toc.constants.TocNodeType;
import org.pageflow.book.domain.toc.entity.TocNode;

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
  private final TocNode tocNode;

  private TreeNode parent;
  private final List<TreeNode> children = new ArrayList<>(10);

  public TreeNode(TocNode tocNode) {
    this.tocNode = tocNode;
    this.parent = null;
  }

  public void addChildAccordingToOv(TreeNode node) {
    Preconditions.checkState(!node.isRoot());

    node.parent = this;
    for(int i = 0; i < children.size(); i++) {
      TreeNode ithChild = children.get(i);
      if(ithChild.equals(node)) {
        throw new IllegalArgumentException("해당 자식 노드가 이미 이 folder의 자식입니다.");
      }
      if(ithChild._getOv() > node._getOv()) {
        children.add(i, node);
        return;
      }
    }
    children.add(node);
  }

  public UUID getParentId() {
    return tocNode.isRootFolder() ? null : tocNode.getParentNodeOrNull().getId();
  }

  public boolean isRoot() {
    return tocNode.isRootFolder();
  }

  public TocNodeType getType() {
    return TocNodeType.from(tocNode);
  }

  private int _getOv() {
    return tocNode.getOv();
  }
}
