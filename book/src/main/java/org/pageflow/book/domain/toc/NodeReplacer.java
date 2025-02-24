package org.pageflow.book.domain.toc;

import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.InvalidField;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : sechan
 */
public class NodeReplacer {

  private static final int OV_GAP = OvRebalancer.OV_GAP;

  private final Folder folderProxy;
  private final List<TocNode> nodes;

  /**
   * @param folderId list을 가지고있는 folder의 id
   * @param nodes ov 기준으로 오름차순 정렬된 노드 리스트
   */
  public NodeReplacer(Folder folderOrProxy, List<TocNode> nodes) {
    this.folderProxy = folderOrProxy;

    if(!NodeListAscendingValidator.isAscending(nodes)){
      throw new IllegalArgumentException("nodes ov must be in ascending order");
    }
    this.nodes = new ArrayList<>(nodes);
  }

  public void reorder(int destIndex, TocNode target){
    if(!this.isNodeChildOfThis(target)){
      throw new IllegalArgumentException("node must be in nodes for reorder operation");
    }
    // 전체 list에서 target node를 제거하고 reparent를 실행한다.
    nodes.remove(target);
    reparent(destIndex, target);
  }

  /**
   * target을 destIndex 위치에 삽입하고, 이를 위한 ov값을 결정하여 할당한다.
   * 필요하다면 모든 형제들의 ov값을 재조정하는 rebalancing을 실행한다.
   * @param destIndex 목적지로 갈 index. 함수 호출 당시의 List의 length를 기준으로 0부터 length까지의 값이다.(length와 같은 경우 node가 추가 된 이후에 제일 마지막에 위치하게된다.)
   */
  public void reparent(int destIndex, TocNode target) {
    if(destIndex < 0 || destIndex > nodes.size()){
      throw new FieldValidationException(InvalidField.builder()
        .field("destIndex")
        .reason(FieldReason.OUT_OF_RANGE)
        .value(destIndex)
        .build()
      );
    }
    if(this.isNodeChildOfThis(target)){
      throw new IllegalArgumentException("node must not be in nodes for reparent operation");
    }

    // NODE를 list에 삽입
    nodes.add(destIndex, target);
    Integer prevOvOrNull = destIndex != 0 ? nodes.get(destIndex - 1).getOv() : null;
    Integer nextOvOrNull = destIndex != nodes.size() - 1 ? nodes.get(destIndex + 1).getOv() : null;

    OvRebalancer rebalancer = new OvRebalancer();
    if(rebalancer.isRequireRebalance(prevOvOrNull, nextOvOrNull)){
      rebalancer.rebalance(nodes);
    } else {
      int newOv = this.resolveOv(prevOvOrNull, nextOvOrNull);
      target.setOv(newOv);
    }
    target.changeParentNode(folderProxy);
  }

  /**
   * TocNode가 가지고있는 parentNode는 신경쓰지 않는다.
   * this 클래스가 가진 List를 기준으로 parentNode를 동기화시킨다.
   * @param node
   * @return
   */
  private boolean isNodeChildOfThis(TocNode node){
    boolean isInList = nodes.contains(node);
    assert node.getParentNode() != null;
    return isInList;
  }

  /**
   * over, underflow의 가능성을 고려하지 않음으로, isRequireRebalance()를 통해서 rebalancing이 필요한지 판단한 후에 호출하는 것이 안전하다.
   */
  private int resolveOv(
    @Nullable Integer prevOvOrNull,
    @Nullable Integer nextOvOrNull
  ) {
    // 기존 child가 없는 경우
    if(prevOvOrNull==null && nextOvOrNull==null){
      return 0;

    // 맨 처음에 삽입되는 경우
    } else if(prevOvOrNull == null){
      return nextOvOrNull - OV_GAP;

    // 맨 마지막에 삽입되는 경우
    } else if(nextOvOrNull == null){
      return prevOvOrNull + OV_GAP;

    // 중간에 삽입되는 경우
    } else {
      long resolved = ((long) prevOvOrNull + nextOvOrNull) / 2;
      return (int) resolved;
    }
  }



}
