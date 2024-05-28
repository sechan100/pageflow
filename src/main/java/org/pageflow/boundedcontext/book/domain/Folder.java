package org.pageflow.boundedcontext.book.domain;


import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public class Folder extends AbstractNode {


    public Folder(BookId bookId, NodeId id, Title title) {
        super(bookId, id, title);
    }

}
