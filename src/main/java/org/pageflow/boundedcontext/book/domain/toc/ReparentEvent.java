package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
public class ReparentEvent extends TocEvent {
    private final NodeId newParentId;
    private final NodeId reparentedNodeId;

    public ReparentEvent(
            NodeId newParentId,
            NodeId reparentedNodeId) {
        this.newParentId = newParentId;
        this.reparentedNodeId = reparentedNodeId;
    }

    @Override
    public boolean isOverride(TocEvent e) {
        if(e instanceof ReparentEvent that){
            return reparentedNodeId.equals(that.reparentedNodeId);
        }
        return false;
    }

    public NodeId getNewParentId() {
        return newParentId;
    }

    public NodeId getReparentedNodeId() {
        return reparentedNodeId;
    }
}
