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
import org.pageflow.book.port.out.ReadTocPort;
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
public class TocNodePersistenceAdapter implements EditTocPort, ReadTocPort {
  private final TocNodeJpaRepository repository;

  @Override
  public Toc loadEditableToc(Book book) {
    List<TocNode> allNode = repository.findAllByBookIdAndIsEditable(book.getId(), true);
    if(allNode.isEmpty()) {
      throw new IllegalStateException("책에 editable toc가 존재하지 않습니다. bookId: " + book.getId());
    }
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
  public void deleteFolder(Toc toc, UUID folderId) {
    Collection<TocNode> allNodes = toc.getAllNodes();
    TocNode folder = toc.get(folderId);
    Preconditions.checkState(folder.isFolder());
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
  public void deleteSection(TocNode section) {
    Preconditions.checkState(section.isSection());
    repository.delete(section);
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

  @Override
  public Toc loadReadonlyToc(Book book) {
    List<TocNode> allNode = repository.findAllByBookIdAndIsEditable(book.getId(), false);
    if(allNode.isEmpty()) {
      throw new IllegalStateException("책에 readOnly toc가 존재하지 않습니다. bookId: " + book.getId());
    }
    return new Toc(book, allNode, false);
  }

  @Override
  public Optional<TocNode> readSection(Book book, UUID sectionId) {
    Optional<TocNode> section = repository.findSectionWithContentById(sectionId);
    section.ifPresent(s -> {
      Preconditions.checkState(s.isSection());
      Preconditions.checkState(s.getBook().equals(book));
    });

    return section;
  }
}
