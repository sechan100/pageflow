package org.pageflow.book.persistence;

import org.pageflow.book.domain.book.entity.ShelfItem;
import org.pageflow.common.jpa.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
public interface ShelfItemPersistencePort extends BaseJpaRepository<ShelfItem, Long> {
  ShelfItem findByBookIdAndUserId(UUID bookId, UUID uid);

  void deleteByBookIdAndUserId(UUID bookId, UUID uid);

  @EntityGraph(attributePaths = {"book", "book.author"})
  List<ShelfItem> findBooksByUserId(UUID uid);
}
