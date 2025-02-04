package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * 클래스 이름 함부로 바꾸지 말 것. jpa dto projection으로 사용중
 * @author : sechan
 */
@Getter
public class NodeProjection {
    private final Long id;
    private final Long parentId; // root 노드인 경우 null
    private final int ov;
    private final String title;
    private final Class<? extends NodeJpaEntity> type;

    public NodeProjection(Long id, Long parentId, int ov, String title, Class<? extends NodeJpaEntity> type) {
        this.id = id;
        this.parentId = parentId;
        this.ov = ov;
        this.title = title;
        this.type = type;
    }

    // no projection
    private List<NodeProjection> children;

    public void addChildAccordingToOv(NodeProjection node){
        if(this.type != FolderJpaEntity.class){
            throw new IllegalStateException("Section 타입에는 자식을 추가할 수 없습니다.");
        }
        if(children == null){
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
