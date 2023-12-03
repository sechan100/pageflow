package org.pageflow.domain.book.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class BookWithPreferenceCount extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;
    private Long preferenceCount;

    public BookWithPreferenceCount(Book book, Long preferenceCount) {
        this.book = book;
        this.preferenceCount = preferenceCount;
    }
}
