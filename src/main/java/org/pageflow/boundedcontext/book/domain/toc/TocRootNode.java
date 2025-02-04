package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.shared.TocNodeType;

/**
 * @author : sechan
 */
public class TocRootNode implements TocNode {
    private final NodeId id;
    private final TocNodeType type;

    public TocRootNode(NodeId id, TocNodeType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public NodeId getId() {
        return id;
    }
}
