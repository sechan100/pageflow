package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;

/**
 * @author : sechan
 */
public interface TocPersistencePort {
    TocRoot loadTocRoot(BookId bookId);
    TocRoot saveToc(TocRoot root);
}
