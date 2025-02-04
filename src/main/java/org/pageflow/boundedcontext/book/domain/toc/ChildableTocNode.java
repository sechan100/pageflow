package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.shared.TocNodeType;

/**
 * @author : sechan
 */
public class ChildableTocNode implements TocNode {
    private final NodeId id;
    private final TocNodeType type;
    private NodeId parentId;
    private int ov;

    /**
     * 업데이트가 필요한 node를 추적하기 위한 flag
     * 완벽하게 정확하지는 않아도 된다. 어차피 jpa 수준에서 dirty checking을 하기 때문에 개략적인 변경 정보만을 기록하도록한다.
     */
    private boolean isChanged = false;

    public ChildableTocNode(NodeId id, NodeId parentId, int ov, TocNodeType type) {
        this.id = id;
        this.parentId = parentId;
        this.ov = ov;
        this.type = type;
    }

    @Override
    public NodeId getId() {
        return id;
    }

    public NodeId getParentId() {
        return parentId;
    }

    public boolean isRootNode(){
        return parentId == null;
    }

    public int getOv() {
        return ov;
    }

    public void setOv(int ov) {
        if(this.ov == ov){
            return;
        }
        this.ov = ov;
        change();
    }

    public void setParentId(NodeId parentId) {
        if(this.parentId.equals(parentId)){
            return;
        }
        this.parentId = parentId;
        change();
    }

    private void change(){
        isChanged = true;
    }

    public boolean isChanged(){
        return isChanged;
    }


    /**
     * adapter에서 isChanged를 변경하기 위한 중첩클래스.
     * isChanged를 변경한다는 의사를 명료하게 하기위해서 nested로 뺐다.(로직 도중에바뀌면 위험하니까)
     */
    public class IsChangedResetter {
        public void reset() {
            isChanged = false;
        }
    }

}
