package org.pageflow.boundedcontext.book.domain;

/**
 * 해당 인터페이스의 구현체는 반드시 엔티티일 필요는 없다. Node의 정보를 얻어올 수 있는 모든 node 구현체 스펙
 * @author : sechan
 */
public interface INode {
    NodeId getId();
    Title getTitle();
    TocNodeType getType();
}
