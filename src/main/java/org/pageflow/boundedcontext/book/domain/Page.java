package org.pageflow.boundedcontext.book.domain;

import lombok.Getter;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;
import org.springframework.lang.NonNull;

/**
 * @author : sechan
 */
@Getter
@AggregateRoot
public class Page implements NodeAr {
    private final BookId bookId;
    private final NodeId parentId;
    private final NodeId id;
    private Title title;
    private String content;


    public Page(BookId bookId, NodeId parentId, NodeId id, @NonNull Title title) {
        this.bookId = bookId;
        this.parentId = parentId;
        this.id = id;
        this.title = title;
    }


    public void changeTitle(Title title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
