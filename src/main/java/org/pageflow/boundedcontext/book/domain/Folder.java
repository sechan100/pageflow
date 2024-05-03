package org.pageflow.boundedcontext.book.domain;

import lombok.Getter;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;
import org.pageflow.global.api.code.Code4;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * @author : sechan
 */
@AggregateRoot
public final class Folder extends NodeEntity {
    /**
     * INode의 구현체로는, 하나의 table만 긁어와도되는 TocNode가 주로 사용됨.
     */
    @Getter
    private final List<INode> childNodes = new LinkedList<>();


    public Folder(NodeId id, Title title, @Nullable List<INode> childNodes) {
        super(id, title);
        if(!(childNodes == null || childNodes.isEmpty())){
            this.childNodes.addAll(childNodes);
        }
    }

    public static Folder create(Title title) {
        return new Folder(NodeId.random(), title, null);
    }

    /**
     * node를 다른 folder의 지정된 색인으로 이동시킨다.
     * @param node 부모 folder를 변경할 node
     * @param dest 1-based index
     */
    public void reparent(INode node, int dest) {
        assert node != null;
        validateDestination(dest, childNodes.size() + 1); // 새로운 노드가 추기되므로, size + 1을 기준으로 판단
        childNodes.add(dest-1, node);
    }

    public void reorder(INode node, int dest) {
        assert node != null;
        validateDestination(dest, childNodes.size());
        childNodes.remove(node);
        childNodes.add(dest-1, node);
    }

    public void reorder(List<INode> newOrder){
        Set newOrderIds = new HashSet(newOrder);
        Set currentOrderIds = new HashSet(childNodes);
        if(!newOrderIds.equals(currentOrderIds)){
            throw Code4.INVALID_VALUE.feedback("새로운 순서의 노드 개수가 현재 하위 노드 개수와 다릅니다.");
        }
        Comparator<INode> comparator = Comparator.comparingInt(n -> newOrder.indexOf(n));
        childNodes.sort(comparator);
    }

    @Override
    public TocNodeType getType() {
        return TocNodeType.FOLDER;
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
