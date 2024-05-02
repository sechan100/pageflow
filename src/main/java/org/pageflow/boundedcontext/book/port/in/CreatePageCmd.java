package org.pageflow.boundedcontext.book.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;

/**
 * @author : sechan
 */
@Value
public class CreatePageCmd {
    NodeId parentNodeId;
    Title title;
}
