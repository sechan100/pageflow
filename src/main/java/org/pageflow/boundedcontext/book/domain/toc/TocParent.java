package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;

import java.util.List;

/**
 * @author : sechan
 */
public interface TocParent extends TocNode {

    /**
     * @param dest 0 <= dest < folder.size() 인 정수
     * @param target 재정렬할 노드 참조
     */
    void reorder(int dest, TocChild target);

    /**
     * @param dest 0 <= dest < folder.size() 인 정수
     * @param target 부모 folder를 변경할 node
     */
    void reparent(int dest, TocChild target);

    int size();
    TocChild getChild(NodeId id);
    List<TocChild> getChildren();
    TocNode findNode(NodeId id);
    List<TocChild> flatten();
    boolean isSameTree(TocParent other);
    String drawTree();

    void _addChildLast(TocChild child);
    TocChild _recursiveFindNode(NodeId nodeId);
    void _addAccordingToOv(TocChild child);
    void _removeChild(TocChild child);
}

