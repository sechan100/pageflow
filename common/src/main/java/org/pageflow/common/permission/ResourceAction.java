package org.pageflow.common.permission;

/**
 * enum 타입이 구현해야만한다.
 *
 * 'FULL'을 포함해서는 안된다.
 * @author : sechan
 */
public interface ResourceAction {
  String name();
}
