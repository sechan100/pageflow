package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.book.application.dto.TocDto;
import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
public interface TocUseCase {
    TocDto.Node createFolder(CreateFolderCmd cmd);
    TocDto.Node createPage(CreatePageCmd cmd);
    void reorder(ReorderCmd cmd);
    void reparent(ReparentCmd cmd);
    void deleteNode(NodeId id);
}
