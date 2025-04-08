package org.pageflow.book.domain.toc;

import lombok.Getter;
import org.pageflow.book.application.dto.TocDto;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Getter
public class Toc {
  private final Book book;
  private final Collection<TocNode> allNodes;
  private final Map<UUID, TocNode> nodeMap;
  private final UUID rootFolderId;
  private final boolean isEditableToc;

  public Toc(Book book, Collection<TocNode> allNodes, boolean isEditableToc) {
    this.book = book;
    this.allNodes = Collections.unmodifiableCollection(allNodes);
    // nodeMap 만들기 =======================================
    Map<UUID, TocNode> map = new HashMap<>();
    TocNode root = null;
    for(TocNode node : allNodes) {
      map.put(node.getId(), node);
      if(node.isRootFolder()) {
        if(root != null) {
          throw new IllegalStateException("Root Folder가 2개 이상 존재합니다.");
        }
        root = node;
      }
    }
    if(root == null) {
      throw new IllegalStateException("Root Folder가 없습니다.");
    }
    // =========================================================
    this.nodeMap = Collections.unmodifiableMap(map);
    this.rootFolderId = root.getId();
    this.isEditableToc = isEditableToc;
  }

  public TreeNode buildTree() {
    Map<UUID, TreeNode> treeNodeMap = allNodes.stream().collect(Collectors.toMap(
      node -> node.getId(),
      node -> new TreeNode(node)
    ));
    for(TreeNode n : treeNodeMap.values()) {
      UUID parentId = n.getParentId();
      if(parentId == null) { // root folder인 경우
        continue;
      }
      TreeNode parent = treeNodeMap.get(parentId);
      parent.addChildAccordingToOv(n);
    }

    return treeNodeMap.get(rootFolderId);
  }

  public TocDto.Toc buildTreeDto() {
    // TreeNode -> Dto로 변환
    TreeNode rootTreeNode = buildTree();
    List<TocDto.Node> rootChildren = rootTreeNode.getChildren().stream()
      .map(this::_projectRecursive)
      .toList();

    TocDto.Folder rootDto = new TocDto.Folder(nodeMap.get(rootFolderId), rootChildren);
    return new TocDto.Toc(book.getId(), rootDto);
  }

  public TocNode get(UUID nodeId) {
    TocNode node = nodeMap.get(nodeId);
    if(node == null) {
      throw new IllegalStateException("ID가 " + nodeId + "인 노드를 찾을 수 없습니다");
    }
    return node;
  }

  public boolean isEditableToc() {
    return isEditableToc;
  }

  public boolean isReadOnlyToc() {
    return !isEditableToc;
  }

  private TocDto.Node _projectRecursive(TreeNode p) {
    // Folder
    if(p.isFolder()) {
      List<TocDto.Node> children = p.getChildren().stream()
        .map(c -> _projectRecursive(c))
        .toList();
      return new TocDto.Folder(p.getTocNode(), children);
    }
    // Section
    else {
      return new TocDto.Section(p.getTocNode());
    }
  }
}
