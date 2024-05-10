package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
public abstract class TocNode {
    protected final NodeId id;
    protected int ov; // signed value

    protected TocNode(NodeId id, int ov) {
        this.id = id;
        this.ov = ov;
    }

    abstract TocNode deepClone();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TocNode)) return false;

        TocNode tocNode = (TocNode) o;

        return id.equals(tocNode.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public NodeId getId() {
        return id;
    }

    public int getOv() {
        return ov;
    }
}
