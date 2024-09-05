package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.dto.TocDto;

/**
 * @author : sechan
 */
public interface TocUseCase {
    TocDto.Node createFolder(FolderCreateCmd cmd);
    TocDto.Node createSection(SectionCreateCmd cmd);
    void reorder(ReorderCmd cmd);
    void reparent(ReparentCmd cmd);
    void deleteNode(NodeId id);
    TocDto.Toc queryToc(BookId bookId);
}
