package org.pageflow.boundedcontext.book.port.out;

import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.NodeRegistry;
import org.pageflow.boundedcontext.book.dto.TocDto;

import java.util.Optional;


/**
 * @author : sechan
 */
public interface TocPersistencePort {
    TocDto.Toc queryToc(BookId bookId);
    NodeRegistry loadRegistry(BookId bookId);
    NodeRegistry saveNodes(NodeRegistry nodes);
    NodeId getRootNodeId(BookId bookId);
    Optional<Integer> loadMaxOvAmongSiblings(BookId bookId, NodeId parentNodeId);
}
