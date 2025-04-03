package org.pageflow.book.port.in.review;

import org.pageflow.book.domain.ReviewPermission;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * 단일 책 리소스에 접근하기 위한 {@link ReviewPermission} 객체를 생성하는 팩토리 클래스
 *
 * @author : sechan
 */
public interface ReviewAccessPermitter {
  void setPermission(UUID reviewId, UID uid);
}
