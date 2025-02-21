package org.pageflow.book.domain.toc;

import org.hibernate.Hibernate;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.port.out.jpa.NodePersistencePort;
import org.pageflow.common.validation.FieldReason;
import org.pageflow.common.validation.FieldValidationException;
import org.pageflow.common.validation.InvalidField;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 노드를 특정 폴더의 마지막 인덱스에 삽입해주는 클래스
 * @author : sechan
 */
public class LastIndexInserter {
  private final Folder folderProxy;
  private final Supplier<Optional<Integer>> maxOvSupplier;

  /**
   * Rebalance이 필요한 순간에만 호출하여 모든 형제들의 ov를 재조정한다.
   */
  private final Supplier<List<TocNode>> siblingsSupplier;
  private final int OV_GAP = OvRebalancer.OV_GAP;
  private final int OV_START = OvRebalancer.OV_START;


  /**
   * @param bookId
   * @param destFolderId 부모가 될 folder의 아이디
   * @param repo
   */
  public LastIndexInserter(UUID bookId, UUID destFolderId, NodePersistencePort repo) {
    TocNode nodeProxy = (TocNode) Hibernate.unproxy(repo.getReferenceById(destFolderId));
    if(nodeProxy instanceof Folder folder){
      this.folderProxy = folder;
    } else {
      throw new FieldValidationException(InvalidField.builder()
        .field("destFolderId")
        .reason(FieldReason.INVALID_VALUE)
        .message("destFilderid가 folder 타입이 아닙니다.")
        .value(destFolderId)
        .build()
      );
    }
    this.maxOvSupplier = () -> repo.findMaxOvAmongSiblings(bookId, destFolderId);
    this.siblingsSupplier = () -> repo.findChildrenByParentNode_IdOrderByOv(destFolderId);
  }

  /**
   * node를 지정된 folderId의 자식들의 마지막 순서로 배치한다.
   * folderId도 같이 변경한다.
   * 이미 ndoe가 해당 부모에 속해있더라도 마지막 인덱스로 이동시킨다.
   * @param node
   * @return node에 할당된 ov값
   */
  public int insertLast(TocNode node) {
    Optional<Integer> lastIndexOptional = this.maxOvSupplier.get();
    node.changeParentNode(this.folderProxy);

    // folder에 자식이 없음
    if(lastIndexOptional.isEmpty()){
      node.setOv(OV_START);
      return OV_START;
    }

    int lastIndex = lastIndexOptional.get();
    // Rebalance
    OvRebalancer rebalancer = new OvRebalancer();
    if(rebalancer.isRequireRebalance(lastIndex, null)) {
      List<TocNode> nodes = this.siblingsSupplier.get();
      lastIndex = rebalancer.rebalance(nodes);
    }

    int newOv = lastIndex + OV_GAP;
    node.setOv(newOv);
    return newOv;
  }

}
