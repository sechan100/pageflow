package org.pageflow.common.api;

import org.pageflow.common.user.RoleType;
import org.pageflow.common.user.UID;

/**
 * {@link RequestContext}에서 사용하는 사용자의 데이터를 담은 Principal 객체에 대한 스펙을 정의한다.
 *
 * auth 모듈에서 구현한다.
 * @author : sechan
 */
public interface RequestContextUserPrincipal {
  UID getUid();
  RoleType getRole();
}
