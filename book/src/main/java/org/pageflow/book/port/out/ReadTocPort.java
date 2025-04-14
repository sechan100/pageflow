package org.pageflow.book.port.out;

import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.Toc;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface ReadTocPort {
  Toc loadReadonlyToc(Book book);

  Optional<TocNode> readSection(Book book, UUID sectionId);
}
