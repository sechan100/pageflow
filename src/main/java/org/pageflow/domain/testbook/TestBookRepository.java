package org.pageflow.domain.testbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestBookRepository extends JpaRepository<TestBook, Integer> {
    Page<TestBook> findAll(Pageable pageable);
    Slice<TestBook> findAll(Specification<TestBook> spec, Pageable pageable);
}