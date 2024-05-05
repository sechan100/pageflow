package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.aggregateroot.Folder;
import org.pageflow.boundedcontext.book.domain.aggregateroot.NodeEntity;
import org.pageflow.boundedcontext.book.domain.aggregateroot.Page;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface NodePersistencePort {
    <N extends NodeEntity> N saveNode(N node);

    Optional<NodeEntity> loadNode(NodeId id);
    Optional<Folder> loadFolder(NodeId id);
    Optional<Page> loadPage(NodeId id);

    <N extends NodeEntity> void deleteNode(N node);
}
