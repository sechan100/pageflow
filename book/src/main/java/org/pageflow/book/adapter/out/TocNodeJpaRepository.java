package org.pageflow.book.adapter.out;

import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocNodeJpaRepository extends BaseJpaRepository<TocNode, UUID> {

  List<TocNode> findChildrenByParentNodeIdOrderByOv(UUID folderId);

  @EntityGraph(attributePaths = {"content"})
  Optional<TocNode> findSectionWithContentById(UUID sectionId);

  List<TocNode> findAllByBookIdAndIsEditable(UUID bookId, boolean isEditable);

  List<TocNode> findAllByBookId(UUID bookId);
}