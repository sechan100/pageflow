//package org.pageflow.boundedcontext.book.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.pageflow.boundedcontext.book.constants.BookPolicy;
//import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaEntity;
//import org.pageflow.shared.jpa.BaseJpaEntity;
//
///**
// * @author : sechan
// */
//@Entity
//@Getter
//@Setter(AccessLevel.NONE)
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name = "book")
//public class BookJpaEntity extends BaseJpaEntity {
//
//    @Id
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "profile_id")
//    private ProfileJpaEntity author;
//
//    @Setter
//    @Column(nullable = false)
//    private String title;
//
//    @Column(nullable = false)
//    private int lastNodeOrdinalValue;
//
//    public BookJpaEntity(ProfileJpaEntity author){
//        this.author = author;
//        this.title = BookPolicy.DEFAULT_BOOK_TITLE;
//    }
//
//    public BookJpaEntity getBook(){
//        return this;
//    }
//
//    public int increaseLastNodeOrdinalValueByOffsetAndGet(){
//        return lastNodeOrdinalValue += BookPolicy.ORDINAL_OFFSET;
//    }
//
//    public void addLast(ChildNodeJpaEntity childNodeEntity){
//        // 부모 재지정
//        childNodeEntity.setParentNode(null);
//        // ordinal value 재지정
//        childNodeEntity.setOrdinalValue(increaseLastNodeOrdinalValueByOffsetAndGet());
//    }
//}
