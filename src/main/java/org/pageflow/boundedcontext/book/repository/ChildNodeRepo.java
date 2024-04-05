package org.pageflow.boundedcontext.book.repository;

import jakarta.persistence.OrderBy;
import org.pageflow.boundedcontext.book.entity.BookEntity;
import org.pageflow.boundedcontext.book.entity.ChildNodeEntity;
import org.pageflow.boundedcontext.book.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : sechan
 */
public interface ChildNodeRepo extends JpaRepository<ChildNodeEntity, Long> {
    @OrderBy("parent_id")
    List<ChildNodeEntity> findByBookOrderByParentNode_Id(BookEntity book);

    FolderEntity findByBookAndParentNode(BookEntity book, FolderEntity parentNode);
}
