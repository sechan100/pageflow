//package org.pageflow.boundedcontext.book.repository;
//
//import jakarta.persistence.OrderBy;
//import org.pageflow.boundedcontext.book.entity.BookJpaEntity;
//import org.pageflow.boundedcontext.book.entity.ChildNodeJpaEntity;
//import org.pageflow.boundedcontext.book.entity.FolderJpaEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
///**
// * @author : sechan
// */
//public interface ChildNodeRepo extends JpaRepository<ChildNodeJpaEntity, Long> {
//    @OrderBy("parent_id")
//    List<ChildNodeJpaEntity> findByBookOrderByParentNode_Id(BookJpaEntity book);
//
//    FolderJpaEntity findByBookAndParentNode(BookJpaEntity book, FolderJpaEntity parentNode);
//}
