package org.pageflow.boundedcontext.book.persistence;

import org.pageflow.boundedcontext.book.domain.entity.Section;
import org.pageflow.shared.jpa.repository.BaseJpaRepository;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SectionRepository extends BaseJpaRepository<Section, UUID> {
}
