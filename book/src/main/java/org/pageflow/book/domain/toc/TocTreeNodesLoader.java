package org.pageflow.book.domain.toc;

import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.enums.BookStatus;
import org.pageflow.book.port.out.jpa.NodePersistencePort;

import java.util.List;

/**
 * @author : sechan
 */
public class TocTreeNodesLoader {
  private final NodePersistencePort nodePersistencePort;

  public TocTreeNodesLoader(NodePersistencePort nodePersistencePort) {
    this.nodePersistencePort = nodePersistencePort;
  }

  /**
   * Book의 상태등을 판단하여 적절한 node tree의 node들을 반환한다.
   * 예를 들어서 Book이 {@link org.pageflow.book.domain.enums.BookStatus#REVISING}상태라면 revision toc tree의 node들을 반환한다.
   *
   * @param book
   * @return
   */
  public List<TocNode> loadTocNodes(Book book) {
    boolean findRevisionTocTree = book.getStatus() == BookStatus.REVISING;
    return nodePersistencePort.findAllByBookIdAndIsRevisionToc(book.getId(), findRevisionTocTree);
  }
}
