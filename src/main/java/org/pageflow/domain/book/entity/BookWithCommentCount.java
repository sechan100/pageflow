package org.pageflow.domain.book.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class BookWithCommentCount extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    private Long commentCount;

    public BookWithCommentCount(Book book, Long commentCount) {
        this.book = book;
        this.commentCount = commentCount;
    }
}
