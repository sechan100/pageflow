package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
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
@ToString(callSuper = true)
@Table(name = "page")
public class PageEntity extends ChildNodeEntity {

    @Lob
    @Column(nullable = false)
    private String content;

    public PageEntity(ParentNodeEntity parent){
        super(parent, BookPolicy.DEFAULT_PAGE_TITLE);
        this.content = "BookPolicy.DEFAULT_PAGE_CONTENT;";
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
