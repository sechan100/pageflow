package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.pageflow.boundedcontext.book.constants.BookPolicy;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "folder")
public class FolderEntity extends ChildNodeEntity implements ParentNodeEntity {

    @Column(name = "last_node_ordinal_value", nullable = false)
    private int lastNodeOrdinalValue;

    public FolderEntity(ParentNodeEntity parent){
        super(parent, BookPolicy.DEFAULT_FOLDER_TITLE);
    }

    public boolean isEmpty(){
        return lastNodeOrdinalValue == 0;
    }

    @Override
    public void addLast(ChildNodeEntity childNodeEntity){
        // 부모 재지정
        childNodeEntity.setParentNode(this);
        // ordinal value 재지정
        childNodeEntity.setOrdinalValue(increaseLastNodeOrdinalValueByOffsetAndGet());
    }

    @Override
    public int increaseLastNodeOrdinalValueByOffsetAndGet(){
        return lastNodeOrdinalValue += BookPolicy.ORDINAL_OFFSET;
    }
}
