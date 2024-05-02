package org.pageflow.boundedcontext.book.port.in;

import org.pageflow.boundedcontext.book.application.dto.TocDto;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;

/**
 * @author : sechan
 */
public interface TocUseCase {
    TocDto.Node createFolder(CreateFolderCmd cmd);
    TocDto.Node createPage(CreatePageCmd cmd);
    TocDto.Node changeTitle(NodeId id, Title title);
    TocDto.Node reparent(ReparentCmd cmd);
    TocDto.Node deleteNode(NodeId id);
}
