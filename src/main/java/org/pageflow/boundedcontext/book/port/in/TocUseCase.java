package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.book.application.dto.TocDto;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
public interface TocUseCase {
    TocDto.SingleNode createFolder(CreateFolderCmd cmd);
    TocDto.SingleNode createPage(CreatePageCmd cmd);
    void reorder(ReorderCmd cmd);
    void reparent(ReparentCmd cmd);
    void deleteNode(NodeId id);
    TocDto.Toc queryToc(BookId bookId);
}
