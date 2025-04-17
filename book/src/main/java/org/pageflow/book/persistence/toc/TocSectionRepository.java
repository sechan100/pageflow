package org.pageflow.book.persistence.toc;

import org.pageflow.book.domain.toc.entity.TocSection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocSectionRepository extends JpaRepository<TocSection, UUID> {
  @EntityGraph(attributePaths = {"sectionDetails", "sectionDetails.content"})
  Optional<TocSection> findWithContentById(UUID sectionId);
}