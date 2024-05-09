package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.domain.toc.Toc;
import org.pageflow.boundedcontext.book.port.in.CreateFolderCmd;
import org.pageflow.boundedcontext.book.port.in.CreatePageCmd;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface NodePersistencePort {
    Optional<NodeAr> loadNode(NodeId id);
    Toc loadToc(BookId bookId);

    Folder createFolder(CreateFolderCmd cmd);
    Page createPage(CreatePageCmd cmd);

    <N extends NodeAr> N saveNode(N node);
    Toc saveToc(Toc toc);

    void deleteNode(NodeId id);
}
