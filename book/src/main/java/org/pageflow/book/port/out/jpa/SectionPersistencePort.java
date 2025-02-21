package org.pageflow.book.port.out.jpa;

import org.pageflow.book.domain.entity.Section;
import org.pageflow.common.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SectionPersistencePort extends BaseJpaRepository<Section, UUID> {
}
