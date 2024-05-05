package org.pageflow.boundedcontext.book.domain.aggregateroot;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.book.domain.TocNodeType;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;

/**
 * TocNode인 Folder와 Page를 위한 추상화.
 * @author : sechan
 */
@AggregateRoot
public final class Page extends NodeEntity {
    private String content;


    public Page(NodeId id, Title title) {
        super(id, title);
    }

    public static Page create(Title title) {
        return new Page(NodeId.random(), title);
    }

    @Override
    public TocNodeType getType() {
        return TocNodeType.PAGE;
    }
}
