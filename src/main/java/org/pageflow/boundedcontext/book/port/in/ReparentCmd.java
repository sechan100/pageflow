package org.pageflow.boundedcontext.book.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.book.domain.NodeId;

/**
 * @author : sechan
 */
@Value
public class ReparentCmd {
    NodeId nodeId;
    NodeId destinationFolder;
    int destinationIndex;
}
