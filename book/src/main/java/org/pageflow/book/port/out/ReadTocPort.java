package org.pageflow.book.port.out;

import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.toc.Toc;

/**
 * @author : sechan
 */
public interface ReadTocPort {
  Toc loadReadonlyToc(Book book);
}
