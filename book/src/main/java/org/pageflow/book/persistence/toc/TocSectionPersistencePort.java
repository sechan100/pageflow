package org.pageflow.book.persistence.toc;

import org.pageflow.book.domain.toc.entity.TocSection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocSectionPersistencePort extends JpaRepository<TocSection, UUID> {
  @EntityGraph(attributePaths = {"content"})
  Optional<TocSection> findWithContentByIdAndIsEditable(UUID sectionId, boolean isEditable);
}