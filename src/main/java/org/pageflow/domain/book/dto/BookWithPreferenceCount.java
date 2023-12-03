package org.pageflow.domain.book.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.book.entity.Book;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookWithPreferenceCount extends BaseEntity {

    private Book book;
    private Long preferenceCount;

    public BookWithPreferenceCount(Book book, Long preferenceCount) {
        this.book = book;
        this.preferenceCount = preferenceCount;
    }
}
