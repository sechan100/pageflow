package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.Folder;
import org.pageflow.boundedcontext.book.domain.NodeAr;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Page;
import org.pageflow.boundedcontext.book.port.in.CreateFolderCmd;
import org.pageflow.boundedcontext.book.port.in.CreatePageCmd;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface NodePersistencePort {
    Optional<NodeAr> loadNode(NodeId id);
    Folder createFolder(CreateFolderCmd cmd);

    Page createPage(CreatePageCmd cmd);
    <N extends NodeAr> N saveNode(N node);

    void deleteNode(NodeId id);
}
