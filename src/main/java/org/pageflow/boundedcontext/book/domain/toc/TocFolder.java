package org.pageflow.boundedcontext.book.domain.toc;

import io.vavr.control.Option;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.global.flow.code.Case3;
import org.pageflow.global.flow.code.DomainException;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author : sechan
 */
public final class TocFolder extends TocNode {
    public static final int OV_OFFSET = 10_000_000;
    // package-private
    final List<TocNode> children;



    public TocFolder(NodeId id, int ov) {
        this(id, ov, null);
    }

    public TocFolder(NodeId id){
        this(id, 0);
    }

    public TocFolder(NodeId id, int ov, @Nullable List<TocNode> childNodes) {
        super(id, ov);
        if(!(childNodes==null || childNodes.isEmpty())){
            this.children = new LinkedList<>(childNodes);
        } else {
            this.children = new LinkedList<>();
        }
    }



    public TocNode get(NodeId id) {
        return children.stream()
            .filter(node -> node.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> Case3.DATA_NOT_FOUND.feedback(String.format("해당 id(%d)를 가진 노드를 찾을 수 없습니다.", id.toLong())));
    }

    public int size() {
        return children.size();
    }

    public boolean removeChild(TocNode target) {
        return children.remove(target);
    }

    public void addChild(int dest, TocNode target) {
        children.add(dest, target);
        int prevOv;
        int nextOv;

        // 앞뒤 다 없는 경우
        if(children.size() == 1){
            prevOv = 0;
            nextOv = 0;
        // 뒤만 있음
        } else if(dest == 0){
            nextOv = children.get(1).getOv();
            prevOv = nextOv - 2*OV_OFFSET;
        // 앞만 있음
        } else if(dest == children.size()-1){
            prevOv = children.get(dest-1).getOv();
            nextOv = prevOv + 2*OV_OFFSET;
        // 앞뒤 다 있음
        } else {
            prevOv = children.get(dest-1).getOv();
            nextOv = children.get(dest+1).getOv();
        }

        // 추가한 위치의 앞뒤 노드의 ov의 차가 '단위ov(1)'인 경우 -> 더이상 공간이 없음
        if(nextOv - prevOv == 1){
            rebalanceOv();
            return;
        }
        // rebalance가 필요하지 않은 경우
        target.ov = (prevOv + nextOv) / 2; // floor
    }

    private void rebalanceOv(){
        int approximatelyCenter = children.size()/2;
        // 정중앙에 근사한 노드의 ov를 0으로 초기화
        children.get(approximatelyCenter).ov = 0;

        // 홀
        if(size() % 2 != 0){
            for(int i = 1; i <= approximatelyCenter; i++){
                children.get(approximatelyCenter - i).ov = -OV_OFFSET * i;
                children.get(approximatelyCenter + i).ov = +OV_OFFSET * i;
            }
        // 짝
        } else {
            for(int i = 1; i <= approximatelyCenter-1; i++){
                children.get(approximatelyCenter - i).ov = -OV_OFFSET * i;
                children.get(approximatelyCenter + i).ov = +OV_OFFSET * i;
            }
            children.get(0).ov = -OV_OFFSET * approximatelyCenter;
        }
    }

    public void addAccordingToOv(TocNode target) {
        int dest = 0;
        for(TocNode node : children){
            if(node.getOv() > target.getOv()){
                break;
            }
            dest++;
        }
        addChild(dest, target);
    }

    public void addChildLast(TocNode target) {
        addChild(children.size(), target);
    }

    void reorder(int dest, TocNode reorderTarget) {
        removeChild(reorderTarget);
        addChild(dest, reorderTarget);
    }


    /**
     * startFolder에서부터 시작하여 하위 모든 노드를 탐색하여 nodeId와 일치하는 id를 가진 node를 찾는다.
     * @param nodeId 찾을 노드의 id
     * @return 찾은 노드
     */
    public TocNode findNode(NodeId nodeId) {
        if(id.equals(nodeId)){
            return this;
        }
        TocNode result = recursiveFindNode(nodeId);
        if(result != null){
            return result;
        } else {
            throw Case3.DATA_NOT_FOUND.feedback(
                "노드를 찾을 수 없습니다. (id: %s)".formatted(nodeId)
            );
        }
    }
    private TocNode recursiveFindNode(NodeId nodeId) {
        for(TocNode node : children){
            if(node.getId().equals(nodeId)){
                return node;
            }
            if(node instanceof TocFolder folder && folder.size() > 0){
                TocNode result = folder.recursiveFindNode(nodeId);
                if(result != null) return result;
            }
        }
        return null;
    }

    /**
     * startFolder에서부터 시작하여 하위 모든 노드를 탐색하여 nodeId와 일치하는 id를 가진 node의 부모 노드를 찾는다.
     * @param nodeId 찾을 노드의 id
     * @return 찾은 노드의 부모 노드
     */
    public Option<TocFolder> findParentNode(NodeId nodeId) {
        if(id.equals(nodeId)){
            throw new IllegalArgumentException("TocFolder는 자기 자신의 부모를 참조할 수 없습니다.");
        }
        TocFolder result = recursiveFindParentNode(nodeId);
        if(result != null){
            return result;
        } else {
            throw DomainException.builder()
                    .apiCode(Case3.DATA_NOT_FOUND)
        }
    }
    private TocFolder recursiveFindParentNode(NodeId nodeId) {
        for(TocNode node : children){
            if(node.getId().equals(nodeId)){
                return this;
            }
            if(node instanceof TocFolder folder && folder.size() > 0){
                TocFolder result = folder.recursiveFindParentNode(nodeId);
                if(result != null) return result;
            }
        }
        return null;
    }

    public Stream<TocNode> chidrenStream(){
        return children.stream();
    }

    public List<TocNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * @return 현재 node의 위치로부터 모든 하위 트리를 복사한다.
     */
    @Override
    public TocNode deepClone(){
        List<TocNode> clonedChildren = new LinkedList<>();
        for(TocNode child : children){
            clonedChildren.add(child.deepClone());
        }
        return new TocFolder(id, ov, clonedChildren);
    }

    public List<TocNode> flatten(){
        List<TocNode> result = new LinkedList<>();
        for(TocNode child : children){
            result.add(child);
            if(child instanceof TocFolder folder){
                result.addAll(folder.flatten());
            }
        }
        return result;
    }

    public List<TocFolder> flattenOnlyFolder() {
        List<TocFolder> result = new LinkedList<>();
        for(TocNode child : children){
            if(child instanceof TocFolder folder){
                result.add(folder);
                result.addAll(folder.flattenOnlyFolder());
            }
        }
        return result;
    }

    public Map<NodeId, TocFolder> indexingDescendantFolders() {
        Map<NodeId, TocFolder> result = new HashMap<>();
        for(TocNode child : children){
            if(child instanceof TocFolder folder){
                result.put(folder.getId(), folder);
                result.putAll(folder.indexingDescendantFolders());
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof TocFolder)) return false;

        TocFolder folder = (TocFolder) o;
        if(!id.equals(folder.id)) return false;
        if(children.size() != folder.children.size()) return false;

        int size = children.size();
        for(int i = 0; i< size; i++){
            if(!children.get(i).equals(folder.children.get(i))){
                return false;
            }
        }

        return true;
    }

    public boolean isReordered(TocFolder original) {
        if(children.size() != original.children.size()) return true;
        int size = children.size();
        for(int i = 0; i< size; i++){
            if(!children.get(i).equals(original.children.get(i))){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(children);
    }
}
