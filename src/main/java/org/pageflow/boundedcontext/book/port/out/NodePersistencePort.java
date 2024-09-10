package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.*;

import java.util.Optional;

/**
 * @author : sechan
 */
public interface NodePersistencePort {
    Folder createFolder(BookId bookId, NodeId parentNodeId, Title title, int ov);
    Section createSection(BookId bookId, NodeId parentNodeId, Title title, String content, int ov);
    Optional<Folder> loadFolder(NodeId id);
    Optional<Section> loadSection(NodeId id);
    Folder saveFolder(Folder folder);
    Section saveSection(Section section);
    void deleteNode(NodeId id);
    Optional<Integer> loadMaxOvAmongSiblings(BookId bookId, NodeId parentNodeId);
}
