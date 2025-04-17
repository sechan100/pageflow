package org.pageflow.book.persistence.toc;

import org.pageflow.book.domain.toc.entity.TocNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocNodeRepository extends JpaRepository<TocNode, UUID> {

  List<TocNode> findAllByBookId(UUID bookId);

  int countByBookIdAndParentNodeIdAndIsEditable(UUID bookId, UUID parentNodeId, boolean isEditable);
}