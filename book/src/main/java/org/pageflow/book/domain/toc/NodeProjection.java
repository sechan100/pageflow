package org.pageflow.book.domain.toc;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 클래스 이름 함부로 바꾸지 말 것. jpa dto projection으로 사용중
 *
 * @author : sechan
 */
@Getter
public class NodeProjection {
  private final UUID id;
  private final UUID parentId; // root 노드인 경우 null
  private final int ov;
  private final String title;
  private final Class<? extends TocNode> type;

  public NodeProjection(UUID id, UUID parentId, int ov, String title, Class<? extends TocNode> type) {
    this.id = id;
    this.parentId = parentId;
    this.ov = ov;
    this.title = title;
    this.type = type;
  }

  // ====================================================
  // 해당 객체로 트리 구조를 만들기 위한 필드와 메서드
  // ====================================================

  // projection field X
  private List<NodeProjection> children = new ArrayList<>();

  public void addChildAccordingToOv(NodeProjection node) {
    Preconditions.checkState(isFolder());

    for(int i = 0; i < children.size(); i++) {
      NodeProjection ithChild = children.get(i);
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
    return type == Folder.class;
  }

  public boolean isSection() {
    return type != Folder.class;
  }


}
