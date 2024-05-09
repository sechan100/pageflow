package org.pageflow.boundedcontext.book.domain.toc;

import io.vavr.control.Try;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;
import org.pageflow.global.api.code.Code3;
import org.pageflow.global.api.code.Code4;

/**
 * Table of Contents aggregate root
 * @author : sechan
 */
@AggregateRoot
public class Toc {
    private final BookId bookId;
    private final TocFolder rootFolder;


    public Toc(BookId bookId, TocFolder rootFolder) {
        this.bookId = bookId;
        this.rootFolder = rootFolder;
    }


    /**
     * @param nodeId 재정렬할 노드의 id
     * @param dest 0 <= dest < folder.size() 인 정수
     */
    public void reorder(NodeId nodeId, int dest) {
        TocFolder folder = findParentNode(rootFolder, nodeId);
        TocNode target = folder.get(nodeId);
        try {
            folder.reorder(dest, target);
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
        if(findNode(rootFolder, folderId) instanceof TocFolder f){
            folder = f;
        } else {
            throw Code3.DATA_NOT_FOUND.feedback("노드의 새로운 부모로 지정된 노드가 '폴더'가 아닙니다.");
        }
        // 이동대상 부모
        TocFolder targetNodeParent = findParentNode(rootFolder, nodeId);
        // 이동대상
        TocNode target = targetNodeParent.get(nodeId);

        // 이동대상이 목적지인 폴더의 부모(직계가 아닌 경우도 포함)인 경우를 검사
        if(target instanceof TocFolder reparentTargetAsFolder){
            Try.of(() -> findNode(reparentTargetAsFolder, folderId))
                .onSuccess(n -> {
                    throw Code4.INVALID_VALUE.feedback("자기자신의 하위 폴더로는 이동할 수 없습니다.");
                });
        }
        // 이동
        targetNodeParent.remove(target);
        folder.add(dest, target);
    }



    /**
     * startFolder에서부터 시작하여 하위 모든 노드를 탐색하여 nodeId와 일치하는 id를 가진 node를 찾는다.
     * 찾을 수 없다면 Optional.empty()를 반환한다.
     * @param startFolder 시작 폴더
     * @param nodeId 찾을 노드의 id
     * @return 찾은 노드
     */
    private static TocNode findNode(TocFolder startFolder, NodeId nodeId) {
        for(TocNode node : startFolder.children){
            if(node.getId().equals(nodeId)){
                return node;
            }
            if(node instanceof TocFolder folder){
                return findNode(folder, nodeId);
            }
        }
        throw Code3.DATA_NOT_FOUND.feedback("해당 id를 가진 노드를 찾을 수 없습니다.");
    }

    /**
     * startFolder에서부터 시작하여 하위 모든 노드를 탐색하여 nodeId와 일치하는 id를 가진 node의 부모 노드를 찾는다.
     * @param startFolder 시작 폴더
     * @param nodeId 찾을 노드의 id
     * @return 찾은 노드의 부모 노드
     */
    private static TocFolder findParentNode(TocFolder startFolder, NodeId nodeId) {
        for(TocNode node : startFolder.children){
            if(node instanceof TocFolder folder){
                if(folder.children.stream().anyMatch(n -> n.getId().equals(nodeId))){
                    return folder;
                }
                return findParentNode(folder, nodeId);
            }
        }
        throw Code3.DATA_NOT_FOUND.feedback("해당 id를 가진 노드의 부모 노드를 찾을 수 없습니다.");
    }
}
