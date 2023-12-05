package org.pageflow.domain.book.constants;

/**
 * @author : sechan
 */
public enum BookStatus {
    
    DRAFT("초안 작성중"), // 초안 작성중
    REVIEW_REQUESTED("검수 요청됨"), // 검수 요청됨
    REVIEWING("검수중"), // 검수 중 (출판 검수중)
    REVIEW_CANCELED("검수 취소됨"), // 검수 취소됨
    PUBLISHED("출판 완료"), // 출판됨
    SUSPENDED("출판 중단"), // 출판 중단됨
    REJECTED("출판 거부"); // 출판 거부됨
    
    
    public final String statusText;
    
    BookStatus(String statusText) {
        this.statusText = statusText;
    }
}
