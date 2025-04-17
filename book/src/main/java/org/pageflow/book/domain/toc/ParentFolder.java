package org.pageflow.book.domain.toc;

import com.google.common.base.Preconditions;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.common.result.Result;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author : sechan
 */
public class ParentFolder {
  private static final int OV_GAP = TocNodeConfig.OV_GAP;
  private final int OV_START = TocNodeConfig.OV_START;

  private final TocFolder folder;

  public ParentFolder(TocFolder tocFolder) {
    Preconditions.checkState(tocFolder.isEditable());
    Preconditions.checkState(
      isAscending(tocFolder.getChildren()),
      "folder의 자식 TocNode들의 ov가 오름차순으로 정렬되어 있지 않습니다. children: "
        + tocFolder.getChildren().stream()
        .map(TocNode::getOv)
        .toList()
    );

    this.folder = tocFolder;
  }

  /**
   * @param destIndex
   * @param target
   * @return
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우, parent에 target이 없는 경우 등
   */
  public Result reorder(int destIndex, TocNode target) {
    Result<Void> validation = _checkMoveToSelf(target)
      .flatMap(unused -> _checkRootFolderMove(target))
      .flatMap(unused -> _checkDestIndex(destIndex));
    if(validation.isFailure()) {
      return validation;
    }

    if(!folder.getChildren().contains(target)) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, "순서를 변경할 노드가 폴더의 자식이 아닙니다.");
    }

    folder.removeChild(target);
    return _insertNode(destIndex, target);
  }

  /**
   * @param destIndex
   * @param target
   * @return
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우, 이미 parent에 target이 속한 경우 등
   */
  public Result reparent(int destIndex, TocNode target) {
    Result validation = _checkMoveToSelf(target)
      .flatMap(unused -> _checkRootFolderMove(target));
    if(validation.isFailure()) {
      return validation;
    }

    // 이미 parent에 속해있는 경우
    if(folder.getChildren().contains(target)) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, "targetNode가 이미 parent에 속해있습니다.");
    }

    // 계층 구조 파괴 검사
    if(target instanceof TocFolder targetAsFolder) {
      // this에서 출발해서 조상중에 targetFolder가 있는지 검증한다.
      if(_isAncestorOf(targetAsFolder, folder)) {
        return Result.of(BookCode.TOC_HIERARCHY_ERROR, "toc의 계층 구조를 파괴하는 이동입니다.");
      }
    }

    // destIndex 검사
    Result checkDestIndexResult = _checkDestIndex(destIndex);
    if(checkDestIndexResult.isFailure()) return checkDestIndexResult;

    TocFolder targetParent = target.getParentNodeOrNull();
    assert targetParent != null;
    targetParent.removeChild(target);
    return _insertNode(destIndex, target);
  }

  /**
   * node를 지정된 folderId의 자식들의 마지막 순서로 삽입한다.
   *
   * @param node 부모가 지정되지 않은 TocNode
   * @return node에 할당된 ov값
   */
  public int insertLast(TocNode node) {
    Preconditions.checkState(node.getParentNodeOrNull() == null);

    List<TocNode> children = folder.getChildren();
    boolean isEmpty = children.isEmpty();
    // folder에 삽입
    folder.addChild(children.size(), node);

    // folder에 자식이 없는 경우
    if(isEmpty) {
      node.setOv(OV_START);
      return OV_START;
    }

    int lastIndexNodeOv = children.stream()
      .map(TocNode::getOv)
      .max(Integer::compareTo)
      .get();
    // Rebalance
    OvRebalancer rebalancer = new OvRebalancer();
    if(rebalancer.isRequireRebalance(lastIndexNodeOv, null)) {
      lastIndexNodeOv = rebalancer.rebalance(children);
    }
    int newOv = lastIndexNodeOv + OV_GAP;
    node.setOv(newOv);
    return newOv;
  }

  /**
   * target을 destIndex 위치에 삽입하고, 이를 위한 ov값을 결정하여 할당한다.
   * 필요하다면 모든 형제들의 ov값을 재조정하는 rebalancing을 실행한다.
   *
   * @param destIndex 목적지로 갈 index. 함수 호출 당시의 List의 length를 기준으로 0부터 length까지의 값이다.
   * @param target    삽입대상. this.childen에서 빼고나서 호출할 것.
   */
  private Result _insertNode(int destIndex, TocNode target) {
    List<TocNode> children = folder.getChildren();
    assert !children.contains(target) : "target이 parent에 속해있는 경우 relocate 할 수 없습니다.";

    // child 삽입
    folder.addChild(destIndex, target);

    // ==============================================
    // Rebalancing 검사
    Integer prevOvOrNull = destIndex != 0 ? children.get(destIndex - 1).getOv() : null;
    Integer nextOvOrNull = destIndex != children.size() - 1 ? children.get(destIndex + 1).getOv() : null;

    OvRebalancer rebalancer = new OvRebalancer();
    if(rebalancer.isRequireRebalance(prevOvOrNull, nextOvOrNull)) {
      rebalancer.rebalance(children);
    } else {
      int newOv = _resolveOv(prevOvOrNull, nextOvOrNull);
      target.setOv(newOv);
    }

    return Result.ok();
  }

  /**
   * over, underflow의 가능성을 고려하지 않음으로, isRequireRebalance()를 통해서 rebalancing이 필요한지 판단한 후에 호출하는 것이 안전하다.
   */
  private int _resolveOv(
    @Nullable Integer prevOvOrNull,
    @Nullable Integer nextOvOrNull
  ) {
    // 기존 child가 없는 경우
    if(prevOvOrNull == null && nextOvOrNull == null) {
      return 0;

      // 맨 처음에 삽입되는 경우
    } else if(prevOvOrNull == null) {
      return nextOvOrNull - OV_GAP;

      // 맨 마지막에 삽입되는 경우
    } else if(nextOvOrNull == null) {
      return prevOvOrNull + OV_GAP;

      // 중간에 삽입되는 경우
    } else {
      long resolved = ((long) prevOvOrNull + nextOvOrNull) / 2;
      return (int) resolved;
    }
  }

  private static boolean _isSameNode(TocNode n1, TocNode n2) {
    return n1.getId().equals(n2.getId());
  }

  /**
   * target이 this와 동일한 노드인지 검사한다.
   *
   * @code TOC_HIERARCHY_ERROR: node는 자기 자신의 자식이 될 수 없습니다.
   */
  private Result<Void> _checkMoveToSelf(TocNode target) {
    if(_isSameNode(target, folder)) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, "node는 자기 자신의 자식이 될 수 없습니다.");
    }
    return Result.ok();
  }

  /**
   * target이 root folder인지 검사한다.
   *
   * @code TOC_HIERARCHY_ERROR: root folder는 이동할 수 없습니다.
   */
  private static Result _checkRootFolderMove(TocNode target) {
    if(target.isRootFolder()) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, "root folder는 이동할 수 없습니다.");
    }
    return Result.ok();
  }

  /**
   * @param destIndex
   * @return
   * @code TOC_HIERARCHY_ERROR: destIndex가 0보다 작거나 destFolderChildrenSize보다 큰 경우
   */
  private Result _checkDestIndex(int destIndex) {
    if(destIndex < 0 || destIndex > folder.getChildren().size()) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, "destIndex가 올바르지 않습니다.");
    }

    return Result.ok();
  }

  /**
   * ancestor가 descendant의 조상인지 검사한다.
   *
   * @throws IllegalStateException ancestor가 folder가 아닌 경우
   */
  private static boolean _isAncestorOf(TocFolder ancestor, TocNode descendant) {
    if(descendant.isRootFolder()) {
      return false;
    }
    TocFolder actualDescendantParent = descendant.getParentNodeOrNull();
    assert actualDescendantParent != null;
    if(actualDescendantParent.equals(ancestor)) {
      return true;
    } else {
      return _isAncestorOf(ancestor, actualDescendantParent);
    }
  }

  public static boolean isAscending(List<TocNode> children) {
    for(int i = 0; i < children.size() - 1; i++) {
      if(!(children.get(i).getOv() < children.get(i + 1).getOv())) {
        return false;
      }
    }
    return true;
  }

}
