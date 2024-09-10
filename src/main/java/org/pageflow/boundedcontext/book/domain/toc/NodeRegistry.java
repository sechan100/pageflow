package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
public class NodeRegistry {
    private final BookId bookId;
    private final Map<NodeId, TocNode> nodes;

    public NodeRegistry(BookId bookId, Collection<TocNode> nodes){
        this.bookId = bookId;
        this.nodes = nodes.stream()
            .collect(Collectors.toMap(TocNode::getId, node -> node));
    }

    public TocNode findNode(NodeId id) {
        return nodes.get(id);
    }

    public Collection<TocNode> all(){
        return nodes.values();
    }

    public TocParent buildTocParent(NodeId parentId){
        return new TocParent(parentId, this);
    }

    public TocParent buildTocParentFromChildId(NodeId childId){
        TocNode child = findNode(childId);
        return buildTocParent(child.getParentId());
    }

    /**
     * 재귀적으로 계층 구조를 탐색하여 실제로 조상, 또는 후손 관계인지 확인한다.
     * @param parentId 부모 노드의 id
     * @param childId 자식 노드의 id
     * @return child가 parent의 후손인지 여부
     */
    public boolean isDescendantOf(NodeId parentId, NodeId childId){
        TocNode child = findNode(childId);
        if(child.getParentId().equals(parentId)){
            return true;
        } else {
            return isDescendantOf(parentId, child.getParentId());
        }
    }

    public Set<TocNode> getChangedNodes() {
        return all().stream()
            .filter(TocNode::isChanged)
            .collect(Collectors.toSet());
    }
}
