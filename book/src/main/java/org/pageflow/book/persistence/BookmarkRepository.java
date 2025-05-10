package org.pageflow.book.persistence;

import org.pageflow.book.domain.book.entity.Bookmark;
import org.pageflow.common.jpa.repository.BaseJpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookmarkRepository extends BaseJpaRepository<Bookmark, Long> {
  Optional<Bookmark> findByUserIdAndBookId(UUID uid, UUID bookId);
}
