package org.pageflow.boundedcontext.book.domain.toc;

import lombok.Getter;
import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
@Getter
public abstract class TocNode {
    protected final NodeId id;
    protected int ordinal;

    protected TocNode(NodeId id, int ordinal) {
        this.id = id;
        this.ordinal = ordinal;
    }

    abstract protected TocNode deepClone();

}
