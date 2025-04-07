package org.pageflow.book.domain.config;

/**
 * @author : sechan
 */
public abstract class TocNodeConfig {
  public static final String READONLY_ROOT_NODE_TITLE = ":readonly_root";
  public static final String EDITABLE_ROOT_NODE_TITLE = ":editable_root";

  /**
   * ov 값 사이의 차의 최솟값.
   * rebalancing을 판단할 때, 배치될 index 앞위의 ov의 차가 MIN_OV_GAP보다 작다면 rebalancing이 필요하다고 판단한다.
   * 범위: [2, Integer.MAX_VALUE]
   */
  public static final int MIN_OV_GAP = 2;

  /**
   * OV_GAP은 node가 list의 처음과 끝에 삽입될 때, edge node를 기준으로 OV의 간격을 얼마나 두고 삽입할지에 대한 값이다.
   * 범위: [1, Integer.MAX_VALUE]
   */
  public static final int OV_GAP = 10000;

  /**
   * OV_START은 Rebalancing을 실행할 때 또는 list에 첫 노드가 삽입될 때 가지게될 ov값이다.
   * 범위: [Integer.MIN_VALUE, Integer.MAX_VALUE]
   */
  public static final int OV_START = 0;
}
