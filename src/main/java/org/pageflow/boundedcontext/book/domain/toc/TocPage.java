package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;

/**
 * @author : sechan
 */
@AggregateRoot
public final class TocPage extends TocNode {

    public TocPage(NodeId id, int ov){
        super(id, ov);
    }

    public TocPage(NodeId id){
        this(id, 0);
    }



    @Override
    protected TocNode deepClone() {
        return new TocPage(id, ov);
    }

}
