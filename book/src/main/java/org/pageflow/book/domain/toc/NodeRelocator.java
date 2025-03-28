package org.pageflow.book.domain.toc;

import com.google.common.base.Preconditions;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.common.result.MessageData;
import org.pageflow.common.result.Result;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * @author : sechan
 */
public class NodeRelocator {

  private static final int OV_GAP = OvRebalancer.OV_GAP;
  private final Folder parent;

  /**
   * @param folderWithChildren children을 모두 초기화한 folder. children은 ov 기준으로 오름차순 정렬되어야한다.
   *                           만약 children이 초기화되어있지 않다면 lazy loading이 발생할 수 있다.
   */
  public NodeRelocator(Folder folderWithChildren) {
    List<TocNode> children = folderWithChildren.getReadOnlyChildren();
    Preconditions.checkState(
      NodeListAscendingValidator.isAscending(children),
      "nodes ov가 오름차순으로 정렬되어 있지 않습니다."
    );
    this.parent = folderWithChildren;
  }

  /**
   * @param destIndex
   * @param target
   * @return
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우, parent에 target이 없는 경우 등
   */
  public Result reorder(int destIndex, TocNode target) {
    // 자기 자신에게 이동 검사
    Result checkMoveToSelfResult = _checkMoveToSelf(target, parent);
    if(checkMoveToSelfResult.isFailure()) return checkMoveToSelfResult;
    // root folder 이동 검사
    Result checkRootFolderMoveResult = _checkRootFolderMove(target);
    if(checkRootFolderMoveResult.isFailure()) return checkRootFolderMoveResult;
    // destIndex 검사
    Result checkDestIndexResult = _checkDestIndex(parent.childrenSize(), destIndex);
    if(checkDestIndexResult.isFailure()) return checkDestIndexResult;

    if(!parent.hasChild(target)) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, MessageData.of("순서를 변경할 노드가 폴더의 자식이 아닙니다."));
    }
    // 전체 list에서 target node를 제거하고 relocate를 실행한다.
    parent.removeChild(target);
    return _relocate(destIndex, target);
  }

  /**
   * @param destIndex
   * @param target
   * @param allBookNodes
   * @return
   * @code TOC_HIERARCHY_ERROR: 자기 자신에게 이동, root folder 이동, 계층 구조 파괴, destIndex가 올바르지 않은 경우, 이미 parent에 target이 속한 경우 등
   */
  public Result reparent(int destIndex, TocNode target, Collection<TocNode> allBookNodes) {
    Map<UUID, TocNode> nodeMap = new HashMap<>();
    assert allBookNodes != null;
    Folder rootFolder = null;
    for(TocNode node : allBookNodes) {
      nodeMap.put(node.getId(), node);
      if(node instanceof Folder folderNode && folderNode.isRootFolder()) {
        assert rootFolder == null;
        rootFolder = folderNode;
      }
    }
    Preconditions.checkNotNull(rootFolder, "root folder가 존재하지 않습니다.");

    // 자기 자신에게 이동 검사
    Result checkMoveToSelfResult = _checkMoveToSelf(target, parent);
    if(checkMoveToSelfResult.isFailure()) return checkMoveToSelfResult;
    // root folder 이동 검사
    Result checkRootFolderMoveResult = _checkRootFolderMove(target);
    if(checkRootFolderMoveResult.isFailure()) return checkRootFolderMoveResult;

    // 이미 parent에 속해있는 경우
    if(parent.hasChild(target)) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, MessageData.of("targetNode가 이미 parent에 속해있습니다."));
    }

    // 계층 구조 파괴 검사
    if(target instanceof Folder targetFolder) {
      // parent에서 출발해서 조상중에 targetFolder가 있는지 검증한다.
      if(_isAncestorOf(targetFolder, this.parent, nodeMap)) {
        return Result.of(BookCode.TOC_HIERARCHY_ERROR, MessageData.of("toc의 계층 구조를 파괴하는 이동입니다."));
      }
    }


    // destIndex 검사
    Result checkDestIndexResult = _checkDestIndex(parent.childrenSize(), destIndex);
    if(checkDestIndexResult.isFailure()) return checkDestIndexResult;
    return _relocate(destIndex, target);
  }

  /**
   * target을 destIndex 위치에 삽입하고, 이를 위한 ov값을 결정하여 할당한다.
   * 필요하다면 모든 형제들의 ov값을 재조정하는 rebalancing을 실행한다.
   *
   * @param destIndex 목적지로 갈 index. 함수 호출 당시의 List의 length를 기준으로 0부터 length까지의 값이다.
   * @param target    삽입대상. this.childen에서 빼고나서 호출할 것.
   */
  private Result _relocate(int destIndex, TocNode target) {
    assert !parent.hasChild(target) : "target이 parent에 속해있는 경우 relocate 할 수 없습니다.";

    // NODE를 list에 삽입
    parent.addChild(destIndex, target);
    // 삽입된 node의 앞
    Integer prevOvOrNull = destIndex != 0 ? parent.getChild(destIndex - 1).getOv() : null;
    // 삽입된 node의 뒤
    Integer nextOvOrNull = destIndex != parent.childrenSize() - 1 ? parent.getChild(destIndex + 1).getOv() : null;

    OvRebalancer rebalancer = new OvRebalancer();
    if(rebalancer.isRequireRebalance(prevOvOrNull, nextOvOrNull)) {
      rebalancer.rebalance(parent.getReadOnlyChildren());
    } else {
      int newOv = this._resolveOv(prevOvOrNull, nextOvOrNull);
      target.setOv(newOv);
    }

    return Result.success();
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
   * @param target
   * @param destFolder
   * @return
   * @code TOC_HIERARCHY_ERROR: node는 자기 자신의 자식이 될 수 없습니다.
   */
  private static Result _checkMoveToSelf(TocNode target, Folder destFolder) {
    if(_isSameNode(target, destFolder)) {
      return org.pageflow.common.result.Result.of(BookCode.TOC_HIERARCHY_ERROR, MessageData.of("node는 자기 자신의 자식이 될 수 없습니다."));
    }

    return org.pageflow.common.result.Result.success();
  }

  /**
   * @param target
   * @return
   * @code TOC_HIERARCHY_ERROR: root folder는 이동할 수 없습니다.
   */
  private static Result _checkRootFolderMove(TocNode target) {
    if(target.isRootFolder()) {
      return org.pageflow.common.result.Result.of(BookCode.TOC_HIERARCHY_ERROR, MessageData.of("root folder는 이동할 수 없습니다."));
    }

    return org.pageflow.common.result.Result.success();
  }

  /**
   * @param destFolderChildrenSize
   * @param destIndex
   * @return
   * @code TOC_HIERARCHY_ERROR: destIndex가 0보다 작거나 destFolderChildrenSize보다 큰 경우
   */
  private static Result _checkDestIndex(int destFolderChildrenSize, int destIndex) {
    if(destIndex < 0 || destIndex > destFolderChildrenSize) {
      return Result.of(BookCode.TOC_HIERARCHY_ERROR, MessageData.of("destIndex가 올바르지 않습니다."));
    }

    return Result.success();
  }

  private static boolean _isAncestorOf(Folder ancestor, Folder descendant, Map<UUID, TocNode> nodeMap) {
    if(descendant.isRootFolder()) {
      return false;
    }
    assert descendant.ensureParentNode() != null;
    UUID parentId = descendant.ensureParentNode().getId();
    if(parentId.equals(ancestor.getId())) {
      return true;
    } else {
      return _isAncestorOf(ancestor, (Folder) nodeMap.get(parentId), nodeMap
      );
    }
  }

}
