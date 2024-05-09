package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.global.api.code.Code3;
import org.springframework.lang.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author : sechan
 */
public final class TocFolder extends TocNode {
    final List<TocNode> children;



    public TocFolder(NodeId id, int ordinal) {
        this(id, ordinal, null);
    }

    public TocFolder(NodeId id, int ordinal, @Nullable List<TocNode> childNodes) {
        super(id, ordinal);
        if(!(childNodes==null || childNodes.isEmpty())){
            this.children = new LinkedList<>(childNodes);
        } else {
            this.children = new LinkedList<>();
        }
    }



    TocNode get(NodeId id) {
        return children.stream()
            .filter(node -> node.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback(String.format("해당 id(%d)를 가진 노드를 찾을 수 없습니다.", id.toLong())));
    }

    public boolean remove(TocNode target) {
        return children.remove(target);
    }

    public void add(int dest, TocNode target) {
        children.add(dest, target);
    }

    public void addAccordingToOrdinal(TocNode target) {
        int dest = 0;
        for(TocNode node : children){
            if(node.getOrdinal() > target.getOrdinal()){
                break;
            }
            dest++;
        }
        add(dest, target);
    }

    public void addLast(TocNode target) {
        children.add(target);
    }

    public void reorder(int dest, TocNode reorderTarget) {
        children.remove(reorderTarget);
        children.add(dest, reorderTarget);
    }



    public TocNode cloneTree() {
        return deepClone();
    }
    /**
     * @return 현재 node의 위치로부터 모든 하위 트리를 복사한다.
     */
    @Override
    protected TocNode deepClone(){
        List<TocNode> clonedChildren = new LinkedList<>();
        for(TocNode child : children){
            clonedChildren.add(child.deepClone());
        }
        return new TocFolder(id, ordinal, clonedChildren);
    }

}
