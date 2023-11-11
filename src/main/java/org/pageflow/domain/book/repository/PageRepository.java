package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.outline.PageSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

    @Query("SELECT new org.pageflow.domain.book.model.outline.PageSummary(p.id, p.title, p.sortPriority, p.chapter.id) " +
            "FROM Page p WHERE p.chapter.id IN :ids " +
            "ORDER BY p.chapter.id ASC, p.sortPriority ASC")
    List<PageSummary> findAllByChapterIdIn(@Param("ids") Collection<Long> ids);


}
