package org.pageflow.boundedcontext.book.domain.toc;

/**
 * @author : sechan
 */
public interface TocChild extends TocNode {
    TocParent getParent();
    int getOv();

    void _registerMoved();
    void _setParent(TocParent parent);
    void _setOv(int ov);
}