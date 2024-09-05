package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;
import org.pageflow.boundedcontext.book.dto.TocDto;

/**
 * @author : sechan
 */
public interface TocPersistencePort {
    TocDto.Toc queryToc(BookId bookId);
    TocRoot loadTocRoot(BookId bookId);
    TocRoot saveToc(TocRoot root);
}
