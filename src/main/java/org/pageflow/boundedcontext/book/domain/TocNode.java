package org.pageflow.boundedcontext.book.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Folder인지 Page인지 크게 중요하지 않은, 단지 해당 위치에 노드가 존재한다는 것을 표현하기위한 값 객체
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public final class TocNode implements INode {
    private final NodeId id;
    private final Title title;
    private final TocNodeType type;
}
