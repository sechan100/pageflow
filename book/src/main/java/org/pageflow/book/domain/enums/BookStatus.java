package org.pageflow.book.domain.enums;

/**
 *
 2. 상태
 - DRAFT: 비공개, 편집가능
 - PUBLISHED: 공개, 편집불가능
 - REVISING: 공개, 편집가능
 - PRIVATE_REVISING: 비공개, 편집가능
 - HIDDEN: 비공개, 편집불가능
 * @author : sechan
 */
public enum BookStatus {
    DRAFT            // 초안작성(비공개, 수정가능)
  , PUBLISHED        // 출판(공개, 수정불가능)
  , REVISING         // 개정중(공개, 수정가능)
  , PRIVATE_REVISING // 비공개 개정중(비공개, 수정가능)
  , HIDDEN           // 출판 숨김(비공개, 수정불가능)
  ;
}
