package org.pageflow.boundedcontext.book.domain.toc;

import io.vavr.collection.Tree;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.springframework.util.Assert;

/**
 * @author : sechan
 */
public abstract class AbstractChild implements TocNode, TocChild {
    private final NodeId id;
    private TocParent parent;
    private int ov;


    protected AbstractChild(NodeId id, TocParent parent, int ov) {
        Assert.notNull(id, "id는 null일 수 없습니다.");
        this.id = id;
        this.parent = parent;
        this.ov = ov;
    }


    @Override
    public TocParent getParent() {
        return parent;
    }

    @Override
    public void _setParent(TocParent parent) {
        this.parent = parent;
    }

    @Override
    public int getOv() {
        return ov;
    }

    @Override
    public void _setOv(int ov) {
        this.ov = ov;
    }

    /**
     * 형제 노드인 경우 false.
     */
    @Override
    public boolean _isDescendantOf(TocParent parent) {
        return this.parent.equals(parent) || this.parent._isDescendantOf(parent);
    }

    @Override
    public NodeId getId() {
        return id;
    }

    @Override
    public Tree.Node<NodeId> _toVavrTree() {
        return Tree.of(id);
    }

    @Override
    public void _registerMoved() {
        _getRoot().registerMoved(this);
    }

    @Override
    public TocRoot _getRoot() {
        if(parent instanceof TocRoot root) return root;
        return parent._getRoot();
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TocNode)) return false;
        TocNode node = (TocNode) o;
        return id.equals(node.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
