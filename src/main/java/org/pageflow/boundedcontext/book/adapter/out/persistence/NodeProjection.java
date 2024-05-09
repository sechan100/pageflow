package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.Data;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeJpaEntity;

/**
 * @author : sechan
 */
@Data
public class NodeProjection {
    private final Long id;
    private final Long parentId;
    private final int ordinal;
    private final Class<? extends NodeJpaEntity> type;
}
