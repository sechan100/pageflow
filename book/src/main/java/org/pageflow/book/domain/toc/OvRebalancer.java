package org.pageflow.book.domain.toc;

import org.pageflow.book.domain.config.TocNodeConfig;
import org.pageflow.book.domain.entity.TocNode;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 한 folder에 있는 모든 자식들의 ov값을 새롭게 초기화해주는 클래스.
 * 구체적으로는 기존의 순서를 유지하면서, OV_GAP에 맞춰서 일정한 간격으로 모든 ov값들을 초기화한다.
 *
 * @author : sechan
 */
public class OvRebalancer {
  private static final int MIN_OV_GAP = TocNodeConfig.MIN_OV_GAP;
  private static final int OV_GAP = TocNodeConfig.OV_GAP;
  private static final int OV_START = TocNodeConfig.OV_START;

  /**
   * 전체 리스트의 ov 재조정이 필요한지 판단한다.
   * prevOrNull과 nextOrNull의 경우의 수에 따라서 rebalancing이 필요한지 판단한다.
   * <ul>
   *   <li>1. prev, next: prev와 next 사이에 삽입되는 경우다. next - prev가 MIN_OV_GAP보다 작은지 확인한다.</li>
   *   <li>2. prev, null: list의 마지막에 삽입되는 경우다. prev + OV_GAP이 Integer.MAX_VALUE를 넘어가는지 확인한다.</li>
   *   <li>3. null, next: list의 맨 처음에 삽입되는 경우다. next - OV_GAP이 Integer.MIN_VALUE보다 작은지 확인한다.</li>
   *   <li>4. null, null: EXCEPTION.</li>
   * </ul>
   *
   * @param prevOvOrNull 삽입할 위치를 기준으로 앞에있는 노드의 ov
   * @param nextOvOrNull 삽입할 위치를 기준으로 뒤에있는 노드의 ov
   */
  public boolean isRequireRebalance(
    @Nullable Integer prevOvOrNull,
    @Nullable Integer nextOvOrNull
  ) {
    // list가 비어있는 경우
    if(prevOvOrNull == null && nextOvOrNull == null) {
      return false;

      // list 맨 처음에 들어가는 경우
    } else if(prevOvOrNull == null) {
      if(Integer.MIN_VALUE + OV_GAP > nextOvOrNull) {
        return true;
      }
      // list 마지막에 들어가는 경우
    } else if(nextOvOrNull == null) {
      if(Integer.MAX_VALUE - OV_GAP < prevOvOrNull) {
        return true;
      }
      // 중간에 들어가는 경우
    } else {
      if(nextOvOrNull - prevOvOrNull < MIN_OV_GAP) {
        return true;
      }
    }

    return false;
  }

  /**
   * Rebalancing을 실행한다.
   *
   * @param children 순서대로 새로운 ov를 할당한다. nodes가 비어있는 경우, 아무것도 하지않고 OV_START를 반환한다.
   * @return rebalancing이 끝난 후의 가장 큰 ov값, 즉 list 마지막 인덱스의 node.ov를 반환한다.
   */
  public int rebalance(List<TocNode> children) {
    if(children.isEmpty()) {
      return OV_START;
    }

    int ov = OV_START;
    for(TocNode node : children) {
      node.setOv(ov);
      if(ov > Integer.MAX_VALUE - OV_GAP) {
        throw new IllegalStateException("OV values exceed Integer.MAX_VALUE during rebalance");
      }
      ov += OV_GAP;
    }
    return ov;
  }
}
