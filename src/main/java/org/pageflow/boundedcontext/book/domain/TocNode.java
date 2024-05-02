package org.pageflow.boundedcontext.book.domain;

/**
 * TocNode인 Folder와 Page를 위한 추상화.
 * @author : sechan
 */
public class TocNode {
    private final NodeId id;
    private String title;


    public TocNode(NodeId id, String title) {
        this.id = id;
        this.title = title;
    }


    public NodeId getId() {
        return id;
    }
}
