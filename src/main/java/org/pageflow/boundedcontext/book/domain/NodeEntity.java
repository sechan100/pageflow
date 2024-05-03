package org.pageflow.boundedcontext.book.domain;

import lombok.Getter;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;

/**
 * @author : sechan
 */
@Getter
@AggregateRoot
public abstract class NodeEntity implements INode {
    private final NodeId id;
    private Title title;

    protected NodeEntity(NodeId id, Title title) {
        this.id = id;
        this.title = title;
    }

    public void changeTitle(Title title) {
        this.title = title;
    }
}
