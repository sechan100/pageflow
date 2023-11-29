package org.pageflow.domain.book.constants;

/**
 * @author : sechan
 */
public enum BookStatus {
    DRAFT, // 초안 작성중
    REVIEW_REQUESTED, // 검토 요청됨
    REVIEWING, // 검토 중 (출판 검수중)
    PUBLISHED, // 출판됨
    SUSPENDED, // 출판 중단됨
    REJECTED // 출판 거부됨
}
