package org.pageflow.boundedcontext.book.entity;

/**
 * @author : sechan
 */
public interface ParentNodeEntity {

    int getLastNodeOrdinalValue();

    void addLast(ChildNodeEntity childNodeEntity);

    BookEntity getBook();

    /**
     * lastNodeOrdinalValue를 offset만큼 증가시킨 후, 반환
     */
    int increaseLastNodeOrdinalValueByOffsetAndGet();
}

