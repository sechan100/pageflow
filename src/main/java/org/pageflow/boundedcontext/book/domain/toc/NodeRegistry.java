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
    private final TocRootNode rootNode;
    private final Map<NodeId, ChildableTocNode> nodes;

    public NodeRegistry(BookId bookId, TocRootNode rootNode, Collection<ChildableTocNode> nodes){
        this.bookId = bookId;
        this.rootNode = rootNode;
        this.nodes = nodes.stream()
            .collect(Collectors.toMap(ChildableTocNode::getId, node -> node));
    }

    public TocNode findNode(NodeId id) {
        if(id.equals(rootNode.getId())){
            return rootNode;
        } else {
            return nodes.get(id);
        }
    }

    public ChildableTocNode findChildableNode(NodeId id){
        if(id.equals(rootNode.getId())){
            throw new IllegalArgumentException("루트노드는 ChildableTocNode가 아닙니다.");
        }
        return nodes.get(id);
    }

    public boolean contains(NodeId id){
        if(id.equals(rootNode.getId())){
            return true;
        }
        return nodes.containsKey(id);
    }

    public Collection<ChildableTocNode> allChildables(){
        return nodes.values();
    }

    public TocParent buildTocParent(NodeId id){
        if(!contains(id)){
            throw new IllegalArgumentException("해당 id의 노드가 존재하지 않습니다.");
        }
        return new TocParent(id, this);
    }

    public TocParent buildTocParentFromChildId(NodeId childId){
        if(findNode(childId) instanceof ChildableTocNode child){
            return buildTocParent(child.getParentId());
        }

        throw new IllegalArgumentException("루트노드의 부모는 존재하지 않습니다.");
    }

    /**
     * 재귀적으로 계층 구조를 탐색하여 실제로 조상, 또는 후손 관계인지 확인한다.
     * @param parentId 부모 노드의 id
     * @param childId 자식 노드의 id
     * @return child가 parent의 후손인지 여부
     */
    public boolean isDescendantOf(NodeId parentId, NodeId childId){
        TocNode childNode = findNode(childId);
        if(childNode instanceof ChildableTocNode child){
            if(child.getParentId().equals(parentId)){
                return true;
            } else {
                return isDescendantOf(parentId, child.getParentId());
            }
        // childNode가 TocRootNode인 경우
        } else {
            return false;
        }

    }

    public Set<ChildableTocNode> getChangedNodes() {
        return allChildables().stream()
            .filter(ChildableTocNode::isChanged)
            .collect(Collectors.toSet());
    }
}
