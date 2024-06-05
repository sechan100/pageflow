package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.application.dto.TocDto;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;

/**
 * @author : sechan
 */
public interface TocPersistencePort {
    TocDto.Toc queryToc(BookId bookId);
    TocRoot loadTocRoot(BookId bookId);
    TocRoot saveToc(TocRoot root);
}
