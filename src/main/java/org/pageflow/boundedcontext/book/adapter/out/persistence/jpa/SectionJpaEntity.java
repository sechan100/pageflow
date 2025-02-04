package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author : sechan
 */
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DiscriminatorValue("section")
@Table(name = "section")
public class SectionJpaEntity extends NodeJpaEntity {

    @Lob
    @Column(nullable = false)
    private String content;

    public SectionJpaEntity(
        Long id,
        BookJpaEntity book,
        String title,
        FolderJpaEntity parentNode,
        String content,
        int ov
    ) {
        super(id, book, title, parentNode, ov);
        this.content = content;
    }

}
