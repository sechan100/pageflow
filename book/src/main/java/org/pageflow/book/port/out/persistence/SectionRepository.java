package org.pageflow.book.port.out.persistence;

import org.pageflow.book.domain.entity.Section;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SectionRepository extends BaseJpaRepository<Section, UUID> {
}
