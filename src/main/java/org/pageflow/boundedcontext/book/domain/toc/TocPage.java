package org.pageflow.boundedcontext.book.domain.toc;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.springframework.lang.NonNull;

/**
 *
 * @author         : sechan
 */
public class TocPage extends AbstractChild implements TocChild {

    public TocPage(NodeId id){
        this(id, null, 0);
    }

    public TocPage(@NonNull NodeId id, TocParent parent, int ov) {
        super(id, parent, ov);
    }

}
