package org.pageflow.domain.book.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Page extends BaseEntity {

    private String content;

    @ManyToOne
    private Book book;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private int pageNumber;

}
