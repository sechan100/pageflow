package org.pageflow.book.adapter.out;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.TocFolder;
import org.pageflow.book.domain.toc.TocSection;
import org.pageflow.book.port.out.EditTocPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class TocPersistenceAdapter implements EditTocPort {
  private final TocNodeJpaRepository repository;

  @Override
  public Toc loadEditableToc(Book book) {
    List<TocNode> allNode = repository.findAllByBookIdAndIsEditable(book.getId(), true);
    return new Toc(book, allNode, true);
  }

  @Override
  public Optional<TocNode> loadEditableNode(Book book, UUID nodeId) {
    Optional<TocNode> node = repository.findById(nodeId);
    node.ifPresent(n -> Preconditions.checkState(n.getBook().equals(book)));
    return node;
  }

  @Override
  public Optional<TocSection> loadEditableSection(Book book, UUID sectionId) {
    Optional<TocNode> section = repository.findSectionWithContentById(sectionId);
    section.ifPresent(n -> Preconditions.checkState(n.getBook().equals(book)));
    return section.map(TocSection::new);
  }

  @Override
  public Optional<TocFolder> loadEditableFolder(Book book, UUID folderId) {
    Optional<TocNode> folder = loadEditableNode(book, folderId);
    return folder.map(n -> {
      List<TocNode> children = repository.findChildrenByParentNodeIdOrderByOv(folderId);
      return new TocFolder(n, children);
    });
  }

  @Override
  public void deleteFolder(Book book, UUID folderId) {
    Toc toc = loadEditableToc(book);
    Collection<TocNode> allNodes = toc.getAllNodes();
    TocNode folder = toc.get(folderId);
    _deleteFolderRecursively(folder, allNodes);
  }

  @Override
  public TocNode persist(TocNode node) {
    return repository.persist(node);
  }

  @Override
  public void deleteAllBookNodes(Book book) {
    List<TocNode> allNode = repository.findAllByBookId(book.getId());
    for(TocNode node : allNode) {
      repository.delete(node);
    }
  }

  @Override
  public void deleteNode(TocNode node) {
    repository.delete(node);
  }

  private void _deleteFolderRecursively(TocNode folder, Collection<TocNode> allNodes) {
    Preconditions.checkState(folder.isFolder());
    List<TocNode> children = allNodes.stream()
      .filter(node -> {
        if(node.isRootFolder()) return false;
        return node.getParentNodeOrNull().equals(folder);
      })
      .toList();

    for(TocNode child : children) {
      if(child.isFolder()) {
        _deleteFolderRecursively(child, allNodes);
      }
      repository.delete(child);
    }
    repository.delete(folder);
  }
}
