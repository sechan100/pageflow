package org.pageflow.domain.book.constants;

/**
 * @author : sechan
 */
public enum BookStatus {
    DRAFT, // 초안 작성중
    REVIEWING, // 검토 중 (출판 검수중)
    APPROVED, // 출판 승인됨 (출판 완료)
    SUSPENDED, // 출판 중단됨
    REJECTED // 출판 거부됨

    
}
