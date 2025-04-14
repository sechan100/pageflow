package org.pageflow.book.port.out;

import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.TocFolder;
import org.pageflow.book.domain.toc.TocSection;

import java.util.Optional;
import java.util.UUID;

/**
 * 해당 port에서는 {@link TocNode#isEditable()}인, 편집 가능한 node들만을 취급한다.
 *
 * @author : sechan
 */
public interface EditTocPort {
  Toc loadEditableToc(Book book);

  Optional<TocNode> loadEditableNode(Book book, UUID nodeId);

  Optional<TocSection> loadEditableSection(Book book, UUID sectionId);

  Optional<TocFolder> loadEditableFolder(Book book, UUID folderId);

  void deleteFolder(Toc toc, UUID folderId);

  TocNode persist(TocNode node);

  /**
   * 책에 걸려있는 모든 TocNode들을 삭제한다.
   * 보통 Book이 삭제될 때 호출.
   */
  void deleteAllBookNodes(Book book);

  void deleteSection(TocNode node);
}
