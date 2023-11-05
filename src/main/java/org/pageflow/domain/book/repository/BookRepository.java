package org.pageflow.domain.book.repository;

import org.pageflow.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);
    
    
    /**
     * EntityGraph.EntityGraphType.LOAD: 지정한 연관관계들을 EAGER 로딩하고, 나머지 연관관계는 엔티티에 정의된 fetch 전략을 따른다.
     * Account의 Profile 같은 경우, 기본 전략이 EAGER이지만, EntityGraph의 default type 값인 'EntityGraphType.FETCH'를 사용하면,
     * 지정한 연관관계가 아닌 모든 연관관계가 엔티티 기본 fetch 전략을 무시하고 모두 LAZY로딩이 된다.
     */
    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.LOAD)
    Book findScopeBookById(Long id);
    
    @EntityGraph(attributePaths = {"author", "chapter"}, type = EntityGraph.EntityGraphType.LOAD)
    Book findScopeChapterById(Long id);
    
    @EntityGraph(attributePaths = {"author", "chapter", "page"}, type = EntityGraph.EntityGraphType.LOAD)
    Book findScopePageById(Long id);
}
