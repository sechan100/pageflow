package org.pageflow.boundedcontext.book.domain.toc;

import io.vavr.control.Try;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;
import org.pageflow.global.api.code.Code3;
import org.pageflow.global.api.code.Code4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Table of Contents aggregate root
 * @author : sechan
 */
@AggregateRoot
public class Toc {
    private final BookId bookId;
    private final TocFolder root;
    private final List<TocEvent> events;


    public Toc(BookId bookId, TocFolder root) {
        this.bookId = bookId;
        this.root = root;
        this.events = new ArrayList<>();
    }



    /**
     * @param nodeId 재정렬할 노드의 id
     * @param dest 0 <= dest < folder.size() 인 정수
     */
    public void reorder(NodeId nodeId, int dest) {
        TocFolder folder = root.findParentNode(nodeId);
        TocNode target = folder.get(nodeId);
        try {
            folder.reorder(dest, target);
            raiseEvent(new ReorderEvent(nodeId));
        } catch(IndexOutOfBoundsException e){
            throw Code4.INVALID_VALUE.feedback("재정렬 대상 인덱스가 범위를 벗어났습니다.", e);
        }
    }

    /**
     * node를 다른 folder의 지정된 색인으로 이동시킨다.
     * @param nodeId 부모 folder를 변경할 node
     * @param dest 0 <= dest < folder.size() 인 정수
     */
    public void reparent(NodeId folderId, NodeId nodeId, int dest) {
        // 이동목표
        TocFolder folder;
        if(root.findNode(folderId) instanceof TocFolder f){
            folder = f;
        } else {
            throw Code3.DATA_NOT_FOUND.feedback("노드의 새로운 부모로 지정된 노드가 '폴더'가 아닙니다.");
        }
        // 이동대상 부모
        TocFolder targetNodeParent = root.findParentNode(nodeId);
        // 이동대상
        TocNode target = targetNodeParent.get(nodeId);

        // 이동대상이 목적지인 폴더의 부모(직계가 아닌 경우도 포함)인 경우를 검사
        if(target instanceof TocFolder reparentTargetAsFolder){
            Try.of(() -> reparentTargetAsFolder.findNode(folderId))
                .onSuccess(n -> {
                    throw Code4.INVALID_VALUE.feedback("자기자신의 하위 폴더로는 이동할 수 없습니다.");
                });
        }
        // 이동
        targetNodeParent.removeChild(target);
        folder.addChild(dest, target);
        raiseEvent(new ReparentEvent(folderId, nodeId));
    }


    public TocFolder getRoot() {
        return root;
    }

    public List<TocEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    private void raiseEvent(TocEvent event){
        events.stream()
            .filter(e -> event.isOverride(e))
            .findFirst()
            .ifPresent(events::remove);

        events.add(event);
    }
}
