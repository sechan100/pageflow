package org.pageflow.boundedcontext.book.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Table of Contents aggregate root
 * @author : sechan
 */
public class Toc {
    private final BookId bookId;
    private final List<Page> nodes;


    public Toc(BookId bookId, List<Page> nodes) {
        this.bookId = bookId;
        this.nodes = new LinkedList<>(nodes);
    }

}
