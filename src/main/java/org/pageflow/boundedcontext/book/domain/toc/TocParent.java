package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
public class TocParent {
    public static final int OV_OFFSET = 10_000_000; // 자식 노드의 OV 간격
    private final NodeId parentId;
    private final List<TocNode> children;
    private final NodeRegistry registry;

    TocParent(NodeId parentId, NodeRegistry registry) {
        this.parentId = parentId;
        this.registry = registry;
        this.children = registry.all().stream()
            .filter(node -> node.getParentId().equals(parentId))
            .sorted((a, b) -> Integer.compare(a.getOv(), b.getOv()))
            .collect(Collectors.toList());
    }


    /**
     * @param dest 0 <= dest < children.size() 인 정수
     * @param targetNodeId 재정렬할 노드 id
     */
    public void reorder(int dest, NodeId targetNodeId) {
        TocNode t = registry.findNode(targetNodeId);
        Assert.isTrue(children.contains(t), "해당 노드는 이 폴더의 자식이 아닙니다.");
        remove(t);
        add(dest, t);
    }

    /**
     * @param dest 0 <= dest < folder.size() 인 정수
     * @param targetNodeId 부모 folder를 변경할 node
     */
    public void reparent(int dest, NodeId targetNodeId) {
        TocNode t = registry.findNode(targetNodeId);
        Assert.isTrue(!children.contains(t), "이미 자식인 childNode를 reparent할 수 없습니다.");
        // target이 this의 Ancestor인 경우를 검사
        Assert.isTrue(
            !registry.isDescendantOf(t.getId(), this.parentId),
            "이동 대상이 parent의 조상인 경우, 이동이 불가능합니다.(순환구조로 인해서 계층구조 파괴)"
        );
        add(dest, t);
    }


    public int size(){
        return children.size();
    }


    // ========================== privates =================================

    private boolean remove(NodeId id){
        return children.removeIf(child -> child.getId().equals(id));
    }

    private boolean remove(TocNode child){
        return children.remove(child);
    }

    /**
     * @param child 추가할 노드. 해당 노드는 children에 이미 속한 노드가 아니어야 함
     * @param dest 0 <= dest < children.size() 인 정수
     */
    private void add(int dest, TocNode child){
        Assert.isTrue(!children.contains(child), "해당 노드는 이미 이 폴더의 자식입니다.");
        children.add(dest, child);
        child.setParentId(parentId);
        int prevOv;
        int nextOv;

        try {
            // 앞뒤 다 없는 경우
            if(children.size() == 1){
                prevOv = 0;
                nextOv = 0;
                // 뒤만 있음
            } else if(dest == 0){
                nextOv = children.get(1).getOv();
                prevOv = Math.addExact(nextOv, -2*OV_OFFSET);
                // 앞만 있음
            } else if(dest == children.size()-1){
                prevOv = children.get(dest-1).getOv();
                nextOv = Math.addExact(prevOv, 2*OV_OFFSET);
                // 앞뒤 다 있음
            } else {
                prevOv = children.get(dest-1).getOv();
                nextOv = children.get(dest+1).getOv();
            }
            // 32비트 정수 자료형 언더, 오버플로우
        } catch (ArithmeticException e){
            prevOv = 0;
            nextOv = 1;
        }

        // 추가한 위치의 앞뒤 노드의 ov의 차가 '단위ov(1)'인 경우 -> 더이상 공간이 없음
        if(nextOv - prevOv == 1){
            rebalanceOv();
            return;
        }
        // rebalance가 필요하지 않은 경우
        child.setOv(
            (prevOv + nextOv) / 2 // floor
        );
    }

    private void rebalanceOv(){
        int approximatelyCenter = children.size()/2;
        // 정중앙에 근사한 노드의 ov를 0으로 초기화
        children.get(approximatelyCenter).setOv(0);

        // 홀수
        if(size() % 2 != 0){
            for(int i = 1; i <= approximatelyCenter; i++){
                children.get(approximatelyCenter - i).setOv(-OV_OFFSET * i);
                children.get(approximatelyCenter + i).setOv(+OV_OFFSET * i);
            }
        // 짝수
        } else {
            for(int i = 1; i <= approximatelyCenter-1; i++){
                children.get(approximatelyCenter - i).setOv(-OV_OFFSET * i);
                children.get(approximatelyCenter + i).setOv(+OV_OFFSET * i);
            }
            children.get(0).setOv(-OV_OFFSET * approximatelyCenter);
        }
    }
}
