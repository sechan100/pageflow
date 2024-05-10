package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.Value;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeJpaEntity;

/**
 * @author : sechan
 */
@Value
public class NodeProjection {
    Long id;
    Long parentId;
    int ov;
    Class<? extends NodeJpaEntity> type;
}
