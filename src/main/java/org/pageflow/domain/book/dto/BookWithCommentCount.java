package org.pageflow.domain.book.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.book.entity.Book;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookWithCommentCount extends BaseEntity {

    private Book book;
    private Long commentCount;

    public BookWithCommentCount(Book book, Long commentCount) {
        this.book = book;
        this.commentCount = commentCount;
    }
}
