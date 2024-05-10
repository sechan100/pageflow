package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
public class ReorderEvent extends TocEvent {
    private final NodeId folderId;

    public ReorderEvent(NodeId folderId) {
        this.folderId = folderId;
    }

    @Override
    public boolean isOverride(TocEvent e) {
        if(e instanceof ReorderEvent that){
            return folderId.equals(that.folderId);
        }
        return false;
    }

    public NodeId getFolderId() {
        return folderId;
    }
}
