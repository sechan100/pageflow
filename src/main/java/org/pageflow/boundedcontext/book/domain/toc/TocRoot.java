package org.pageflow.boundedcontext.book.domain.toc;

import io.vavr.collection.Tree;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author : sechan
 */
public class TocRoot implements TocParent {
    private final BookId bookId;
    private final RootParentableComponent delegate;
    private final MovedChildrenSet movedChildren;


    @SuppressWarnings("ThisEscapedInObjectConstruction")
    public TocRoot(BookId bookId, @Nullable List<TocChild> children) {
        this.bookId = bookId;
        this.delegate = new RootParentableComponent(this, children);
        this.movedChildren = new MovedChildrenSet();
    }


    void registerMoved(TocChild child) {
        movedChildren.add(child);
    }

    public Set<TocChild> flushMovedChildren() {
        return movedChildren.flush();
    }



    public BookId getBookId() {
        return bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TocRoot)) return false;

        TocRoot that = (TocRoot) o;

        return bookId.equals(that.bookId);
    }

    @Override
    public int hashCode() {
        return bookId.hashCode();
    }




    ///// FOLDER //////
    @Override
    public void reorder(int dest, TocChild target) {
        delegate.reorder(dest, target);
    }

    @Override
    public void reparent(int dest, TocChild target) {
        delegate.reparent(dest, target);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public TocChild getChild(NodeId id) {
        return delegate.getChild(id);
    }

    @Override
    public List<TocChild> getChildren() {
        return delegate.getChildren();
    }

    @Override
    public TocNode findNode(NodeId id) {
        return delegate.findNode(id);
    }

    @Override
    public void _addAccordingToOv(TocChild child) {
        delegate._addAccordingToOv(child);
    }

    @Override
    public List<TocChild> flatten() {
        return delegate.flatten();
    }

    @Override
    public boolean isSameTree(TocParent other) {
        return delegate.isSameTree(other);
    }

    @Override
    public String drawTree() {
        return delegate.drawTree();
    }

    @Override
    public void _addChildLast(TocChild child) {
        delegate._addChildLast(child);
    }

    @Override
    public TocChild _recursiveFindNode(NodeId nodeId) {
        return delegate._recursiveFindNode(nodeId);
    }

    @Override
    public void _removeChild(TocChild child) {
        delegate._removeChild(child);
    }

    @Override
    public NodeId getId() {
        return delegate.getId();
    }

    @Override
    public boolean _isDescendantOf(TocParent parent) {
        return delegate._isDescendantOf(parent);
    }

    @Override
    public Tree.Node<NodeId> _toVavrTree() {
        return delegate._toVavrTree();
    }

    @Override
    public TocRoot _getRoot() {
        return this;
    }
}

class RootParentableComponent extends TocFolder {
    private static final long ROOT_FOLDER_ID = 0L;
    private final TocRoot root;

    RootParentableComponent(TocRoot root, List<TocChild> children) {
        super(NodeId.from(ROOT_FOLDER_ID), root, 0, children);
        this.root = root;
    }

    public TocRoot _getRoot() {
        return root;
    }

}