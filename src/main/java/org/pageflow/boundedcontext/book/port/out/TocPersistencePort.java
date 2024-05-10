package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.toc.Toc;

/**
 * @author : sechan
 */
public interface TocPersistencePort {
    Toc loadToc(BookId bookId);
    Toc saveToc(Toc toc);
}
