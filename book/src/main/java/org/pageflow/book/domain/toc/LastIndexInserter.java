package org.pageflow.book.domain.toc;

import org.pageflow.book.domain.config.TocNodeConfig;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;

import java.util.List;
import java.util.Optional;

/**
 * 노드를 특정 폴더의 마지막 인덱스에 삽입해주는 클래스
 *
 * @author : sechan
 */
public class LastIndexInserter {
  private final int OV_GAP = TocNodeConfig.OV_GAP;
  private final int OV_START = TocNodeConfig.OV_START;

  private final Folder destFolder;


  public LastIndexInserter(Folder destFolder) {
    this.destFolder = destFolder;
  }

  /**
   * node를 지정된 folderId의 자식들의 마지막 순서로 배치한다.
   * folderId도 같이 변경한다.
   * 이미 ndoe가 해당 부모에 속해있더라도 마지막 인덱스로 이동시킨다.
   *
   * @param node
   * @return node에 할당된 ov값
   */
  public int insertLast(TocNode node) {
    Optional<Integer> lastIndexOptional = destFolder.getReadOnlyChildren().stream()
      .map(TocNode::getOv)
      .max(Integer::compareTo);

    // destFolder에 추가
    this.destFolder.addChild(node);

    // folder에 자식이 없음
    if(lastIndexOptional.isEmpty()) {
      node.setOv(OV_START);
      return OV_START;
    }

    int lastIndexNodeOv = lastIndexOptional.get();
    // Rebalance
    OvRebalancer rebalancer = new OvRebalancer();
    if(rebalancer.isRequireRebalance(lastIndexNodeOv, null)) {
      List<TocNode> nodes = destFolder.getReadOnlyChildren();
      lastIndexNodeOv = rebalancer.rebalance(nodes);
    }

    int newOv = lastIndexNodeOv + OV_GAP;
    node.setOv(newOv);
    return newOv;
  }

}
