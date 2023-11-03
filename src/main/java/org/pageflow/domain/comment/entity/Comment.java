//package org.pageflow.domain.comment.entity;
//
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.ManyToOne;
//import lombok.*;
//import org.pageflow.base.entity.BaseEntity;
//import org.pageflow.domain.hic.test.TestBook;
//import org.pageflow.domain.user.entity.Account;
//
//@Entity
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class Comment extends BaseEntity {
//
//    @Column(columnDefinition = "TEXT")
//    private String content;
//
//    @ManyToOne
//    private TestBook testBook;
//
//    @ManyToOne
//    private Account author;
//
//}
