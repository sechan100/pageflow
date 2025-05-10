package org.pageflow.book.persistence;

import org.pageflow.book.domain.book.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
  Optional<Bookmark> findByUserIdAndBookId(UUID uid, UUID bookId);
}
