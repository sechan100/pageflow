package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@Table(name = "page")
public class PageJpaEntity extends NodeJpaEntity {

    @Lob
    @Column(nullable = false)
    private String content;

    public PageJpaEntity(
        Long id,
        BookJpaEntity book,
        String title,
        @Nullable FolderJpaEntity parentNode,
        String content
    ) {
        super(id, book, title, parentNode);
        this.content = content;
    }

}
