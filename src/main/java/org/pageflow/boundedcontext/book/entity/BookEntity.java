package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.book.constants.BookPolicy;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.global.entity.TsidBaseEntity;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Table(name = "book")
public class BookEntity extends TsidBaseEntity implements ParentNodeEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private Profile author;

    @Setter
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int lastNodeOrdinalValue;

    public BookEntity(Profile author){
        this.author = author;
        this.title = BookPolicy.DEFAULT_BOOK_TITLE;
    }

    @Override
    public BookEntity getBook(){
        return this;
    }

    @Override
    public int increaseLastNodeOrdinalValueByOffsetAndGet(){
        return lastNodeOrdinalValue += BookPolicy.ORDINAL_OFFSET;
    }

    @Override
    public void addLast(ChildNodeEntity childNodeEntity){
        // 부모 재지정
        childNodeEntity.setParentNode(null);
        // ordinal value 재지정
        childNodeEntity.setOrdinalValue(increaseLastNodeOrdinalValueByOffsetAndGet());
    }
}
