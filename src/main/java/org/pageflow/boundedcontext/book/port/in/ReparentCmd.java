package org.pageflow.boundedcontext.book.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
@Value
public class ReparentCmd {
    BookId bookId;
    NodeId nodeId;
    NodeId destfolderId;
    int destOrder;
}
