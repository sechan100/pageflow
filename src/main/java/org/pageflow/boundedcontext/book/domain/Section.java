package org.pageflow.boundedcontext.book.domain;

import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public class Section extends AbstractNode {
    private String content;


    public Section(BookId bookId, NodeId id, Title title, String content) {
        super(bookId, id, title);
        this.content = content;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
