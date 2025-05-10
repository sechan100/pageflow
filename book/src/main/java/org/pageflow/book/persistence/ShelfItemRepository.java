package org.pageflow.book.persistence;

import org.pageflow.book.domain.book.entity.ShelfItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface ShelfItemRepository extends JpaRepository<ShelfItem, Long> {
  ShelfItem findByBookIdAndUserId(UUID bookId, UUID uid);

  void deleteByBookIdAndUserId(UUID bookId, UUID uid);

  @EntityGraph(attributePaths = {"book", "book.author"})
  List<ShelfItem> findBooksByUserId(UUID uid);
}
