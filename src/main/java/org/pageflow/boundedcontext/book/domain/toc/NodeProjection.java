package org.pageflow.boundedcontext.book.domain.toc;

import lombok.Getter;
import org.pageflow.boundedcontext.book.domain.entity.Folder;
import org.pageflow.boundedcontext.book.domain.entity.TocNode;

import java.util.LinkedList;
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

  /*
   * 아래부터는 Projection과는 관계없이, 메모리에 올리고나서 tree 구조를 만들기 위한 메소드와 필드들이다.
   */

  // projection 하지 않는 필드
  private List<NodeProjection> children;

  // ov 순서대로 자식 추가
  public void addChildAccordingToOv(NodeProjection node) {
    if(this.type!=Folder.class){
      throw new IllegalStateException("Section 타입에는 자식을 추가할 수 없습니다.");
    }
    if(children==null){
      this.children = new LinkedList<>();
    }
    for(int i = 0; i < children.size(); i++){
      if(children.get(i).ov > node.ov){
        children.add(i, node);
        return;
      }
    }
    children.add(node);
  }


}
