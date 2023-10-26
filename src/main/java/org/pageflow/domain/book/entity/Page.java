package org.pageflow.domain.book.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.base.entity.BaseEntity;
@Getter
@Setter
@Entity
@Builder
public class Page extends BaseEntity {

    private String content;

    @ManyToOne
    private Book book;
}
