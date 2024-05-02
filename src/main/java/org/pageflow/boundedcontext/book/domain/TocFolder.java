package org.pageflow.boundedcontext.book.domain;

import org.pageflow.global.api.code.Code3;
import org.pageflow.global.api.code.Code4;

import java.util.*;

/**
 * @author : sechan
 */
public class TocFolder extends TocNode {
    private final List<TocNode> childNodes;


    public TocFolder(NodeId id, String title, List<TocNode> childNodes) {
        super(id, title);
        this.childNodes = new LinkedList<>(childNodes);
    }


    /**
     * node를 다른 folder의 지정된 색인으로 이동시킨다.
     * @param node 부모 folder를 변경할 node
     * @param dest 1-based index
     */
    public void reparent(TocNode node, int dest) {
        assert node != null;
        validateDestination(dest, childNodes.size() + 1); // 새로운 노드가 추기되므로, size + 1을 기준으로 판단
        childNodes.add(dest-1, node);
    }

    public void reorder(NodeId nid, int dest) {
        assert nid != null;
        validateDestination(dest, childNodes.size());
        TocNode t = childNodes.stream()
                .filter(n -> n.getId().equals(nid))
                .findFirst()
                .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("재정렬 대상 노드가 하위 노드중에 존재하지 않습니다."));
        childNodes.remove(t);
        childNodes.add(dest-1, t);
    }

    public void reorder(List<NodeId> newOrder){
        Set newOrderIds = new HashSet(newOrder);
        Set currentOrderIds = new HashSet(getChildNodeIds());
        if(!newOrderIds.equals(currentOrderIds)){
            throw Code4.INVALID_VALUE.feedback("새로운 순서의 노드 개수가 현재 하위 노드 개수와 다릅니다.");
        }
        Comparator<TocNode> comparator = Comparator.comparingInt(n -> newOrder.indexOf(n.getId()));
        childNodes.sort(comparator);
    }

    public List<NodeId> getChildNodeIds() {
        return childNodes.stream()
            .map(TocNode::getId)
            .toList();
    }



    private void validateDestination(int dest, int size) {
        if(dest < 1){
            throw Code4.VALUE_OUT_OF_RANGE.feedback("새로운 색인 값은 0보다 큰 정수입니다");
        }
        if(dest > size){
            throw Code4.VALUE_OUT_OF_RANGE.feedback("새로운 색인 값이 현재 노드의 하위 노드 개수보다 큽니다");
        }
    }

}
