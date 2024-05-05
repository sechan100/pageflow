package org.pageflow.boundedcontext.book.domain.aggregateroot;

import lombok.Getter;
import org.pageflow.boundedcontext.book.domain.INode;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;

/**
 * @author : sechan
 */
@Getter
@AggregateRoot
public abstract class NodeEntity implements INode {
    protected final NodeId id;
    protected Title title;

    protected NodeEntity(NodeId id, Title title) {
        this.id = id;
        this.title = title;
    }

    public void changeTitle(Title title) {
        this.title = title;
    }
}
