package org.pageflow.boundedcontext.book.domain.toc;


import io.vavr.collection.Tree;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author : sechan
 */
public class TocFolder extends AbstractChild implements TocParent, TocChild {
    public static final int OV_OFFSET = 10_000_000;

    private final List<TocChild> children;


    public TocFolder(NodeId id){
        this(id, null, 0, null);
    }

    public TocFolder(@NonNull NodeId id, TocParent parent, int ov, List<TocChild> children) {
        super(id, parent, ov);
        this.children = children == null ? new LinkedList<>() : new LinkedList<>(children);
    }


    @Override
    public void reorder(int dest, TocChild target) {
        Assert.isTrue(children.contains(target), "해당 노드는 이 폴더의 자식이 아닙니다.");
        _removeChild(target);
        addChild(dest, target);
        target._registerMoved();
    }

    @Override
    public void reparent(int dest, TocChild target) {
        Assert.isTrue(!children.contains(target), "이미 자식인 childNode를 reparent할 수 없습니다.");
        // target이 this의 Ancestor인 경우를 검사
        if(target instanceof TocParent targetNodeAsParent){
            Assert.isTrue(
                !this._isDescendantOf(targetNodeAsParent),
                "이동 대상이 this의 조상인 경우, 이동이 불가능합니다."
            );
        }
        // 원래의 부모에서 targetNode를 제거
        target.getParent()._removeChild(target);
        addChild(dest, target);
        target._registerMoved();
    }

    /**
     * @throws NoSuchElementException nodeId에 해당하는 노드가 존재하지 않는 경우
     */
    @Override
    public TocChild getChild(NodeId id) {
        return children.stream()
            .filter(node -> node.getId().equals(id))
            .findFirst()
            .get();
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public void _addAccordingToOv(TocChild child) {
        int dest = 0;
        for(TocChild node : children){
            if(node.getOv() > child.getOv()){
                break;
            }
            dest++;
        }
        addChild(dest, child);
    }

    @Override
    public TocNode findNode(NodeId nodeId) {
        if(getId().equals(nodeId)){
            return this;
        }
        TocChild result = _recursiveFindNode(nodeId);
        if(result != null){
            return result;
        } else {
            throw new IllegalArgumentException("toc 트리에 nodeId(%s)에 해당하는 노드가 존재하지 않습니다.".formatted(nodeId));
        }
    }

    @Override
    public List<TocChild> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public List<TocChild> flatten(){
        List<TocChild> result = new LinkedList<>();
        for(TocChild child : children){
            result.add(child);
            if(child instanceof TocParent parent){
                result.addAll(parent.flatten());
            }
        }
        return result;
    }

    @Override
    public boolean isSameTree(TocParent other){
        return this._toVavrTree().equals(other._toVavrTree());
    }

    @Override
    public String drawTree(){
        Tree tree = _toVavrTree();
        return tree.draw();
    }

    @Override
    public void _addChildLast(TocChild child) {
        addChild(children.size(), child);
    }

    private void addChild(int dest, TocChild target) {
        children.add(dest, target);
        setChildParent(target);
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
        target._setOv(
            (prevOv + nextOv) / 2 // floor
        );
    }

    protected void setChildParent(TocChild target){
        target._setParent(this);
    }

    private void rebalanceOv(){
        int approximatelyCenter = children.size()/2;
        // 정중앙에 근사한 노드의 ov를 0으로 초기화
        children.get(approximatelyCenter)._setOv(0);

        // 홀
        if(size() % 2 != 0){
            for(int i = 1; i <= approximatelyCenter; i++){
                children.get(approximatelyCenter - i)._setOv(-OV_OFFSET * i);
                children.get(approximatelyCenter + i)._setOv(+OV_OFFSET * i);
            }
            // 짝
        } else {
            for(int i = 1; i <= approximatelyCenter-1; i++){
                children.get(approximatelyCenter - i)._setOv(-OV_OFFSET * i);
                children.get(approximatelyCenter + i)._setOv(+OV_OFFSET * i);
            }
            children.get(0)._setOv(-OV_OFFSET * approximatelyCenter);
        }
    }

    @Override
    public Tree.Node<NodeId> _toVavrTree(){
        return Tree.of(
            getId(),
            children.stream()
                .map(TocChild::_toVavrTree)
                .toList()
        );
    }

    @Override
    public boolean _isDescendantOf(TocParent parent) {
        return false;
    }

    @Override
    public TocChild _recursiveFindNode(NodeId nodeId) {
        for(TocChild node : children){
            if(node.getId().equals(nodeId)){
                return node;
            }
            if(node instanceof TocParent parent && parent.size() > 0){
                TocChild result = parent._recursiveFindNode(nodeId);
                if(result != null) return result;
            }
        }
        return null;
    }

    @Override
    public void _removeChild(TocChild target) {
        children.remove(target);
        target._setParent(null);
    }
}
