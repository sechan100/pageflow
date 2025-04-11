package org.pageflow.book.adapter.out;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.TreeNode;
import org.pageflow.book.port.out.TocTreePersistencePort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author : sechan
 */
@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class TocTreePersistenceAdapter implements TocTreePersistencePort {
  private final TocNodePersistenceAdapter tocNodePersistenceAdapter;
  private final TocNodeJpaRepository repository;

  @Override
  public boolean existsEditableToc(Book book) {
    int count = repository.countByBookIdAndParentNodeIdAndIsEditable(book.getId(), null, true);
    if(count > 1) {
      throw new IllegalStateException("editableToc root node가 2개 이상 존재합니다. bookId: " + book.getId());
    }
    return count == 1;
  }

  @Override
  public boolean existsReadonlyToc(Book book) {
    int count = repository.countByBookIdAndParentNodeIdAndIsEditable(book.getId(), null, false);
    if(count > 1) {
      throw new IllegalStateException("readonlyToc root node가 2개 이상 존재합니다. bookId: " + book.getId());
    }
    return count == 1;
  }

  @Override
  public Toc copyReadonlyTocToEditableToc(Toc sourceToc) {
    Preconditions.checkArgument(sourceToc.isReadOnlyToc());

    TreeNode rootTreeNode = sourceToc.buildTree();
    Collection<TocNode> copiedNodes = new ArrayList<>(30);
    TocNode copiedRoot = repository.persist(
      new TocNode(rootTreeNode.getTocNode(), null)
    );
    copiedNodes.add(copiedRoot);
    for(TreeNode child : rootTreeNode.getChildren()) {
      copyNodeRecursive(child, copiedRoot, copiedNodes);
    }

    return new Toc(
      sourceToc.getBook(),
      copiedNodes,
      true
    );
  }

  @Override
  public Toc makeTocReadonly(Toc editableToc) {
    return _makeTocOf(editableToc, false);
  }

  @Override
  public Toc makeTocEditable(Toc readonlyToc) {
    return _makeTocOf(readonlyToc, true);
  }

  @Override
  public void deleteToc(Toc toc) {
    tocNodePersistenceAdapter.deleteFolder(toc, toc.getRootFolderId());
  }

  private Toc _makeTocOf(Toc toc, boolean isEditable) {
    Preconditions.checkArgument(
      toc.isEditableToc() != isEditable,
      """
        Toc의 상태가 이미 %s입니다
        """.formatted(isEditable ? "editable" : "readonly")
    );
    Collection<TocNode> allNodes = toc.getAllNodes();
    for(TocNode node : allNodes) {
      node.setEditable(isEditable);
    }
    return new Toc(
      toc.getBook(),
      allNodes,
      isEditable
    );
  }

  private void copyNodeRecursive(TreeNode node, TocNode parentNode, Collection<TocNode> copiedNodes) {
    Preconditions.checkArgument(parentNode.isFolder());

    TocNode copied = repository.persist(
      new TocNode(node.getTocNode(), parentNode)
    );
    copiedNodes.add(copied);

    if(copied.isFolder()) {
      for(TreeNode child : node.getChildren()) {
        copyNodeRecursive(child, copied, copiedNodes);
      }
    }
  }
}
