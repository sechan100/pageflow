package org.pageflow.boundedcontext.book.domain;

/**
 * @author : sechan
 */
public interface NodeAr {
    NodeId getId();
    Title getTitle();
    void changeTitle(Title title);
}
