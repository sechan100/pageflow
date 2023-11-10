package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

}
