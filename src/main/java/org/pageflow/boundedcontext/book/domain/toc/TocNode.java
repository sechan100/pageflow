package org.pageflow.boundedcontext.book.domain.toc;

import io.vavr.collection.Tree;
import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
public interface TocNode {
    NodeId getId();

    boolean _isDescendantOf(TocParent parent);
    Tree.Node<NodeId> _toVavrTree();
    TocRoot _getRoot();
}
