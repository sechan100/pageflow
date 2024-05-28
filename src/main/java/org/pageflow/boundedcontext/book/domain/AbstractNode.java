package org.pageflow.boundedcontext.book.domain;

import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public abstract class AbstractNode {
    private final BookId bookId;
    private final NodeId id;
    private Title title;

    protected AbstractNode(BookId bookId, NodeId id, Title title) {
        this.bookId = bookId;
        this.id = id;
        this.title = title;
    }

    public final void changeTitle(Title title) {
        this.title = title;
    }
}
