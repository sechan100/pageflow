//package org.pageflow.boundedcontext.book.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//import lombok.*;
//import org.pageflow.boundedcontext.book.constants.BookPolicy;
//
///**
// * @author : sechan
// */
//@Entity
//@Getter
//@Setter(AccessLevel.NONE)
//@EqualsAndHashCode(callSuper = true)
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name = "folder")
//public class FolderJpaEntity extends ChildNodeJpaEntity {
//
//    @Column(name = "last_node_ordinal_value", nullable = false)
//    private int lastNodeOrdinalValue;
//
//    public FolderJpaEntity(ParentNodeEntity parent){
//    }
//
//    public boolean isEmpty(){
//        return lastNodeOrdinalValue == 0;
//    }
//
//    public void addLast(ChildNodeJpaEntity childNodeEntity){
//        // 부모 재지정
//        childNodeEntity.setParentNode(this);
//        // ordinal value 재지정
//        childNodeEntity.setOrdinalValue(increaseLastNodeOrdinalValueByOffsetAndGet());
//    }
//
//    public int increaseLastNodeOrdinalValueByOffsetAndGet(){
//        return lastNodeOrdinalValue += BookPolicy.ORDINAL_OFFSET;
//    }
//}
