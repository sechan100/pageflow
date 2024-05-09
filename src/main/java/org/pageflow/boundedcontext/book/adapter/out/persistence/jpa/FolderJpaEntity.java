package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import jakarta.persistence.Entity;
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
@Table(name = "folder")
public class FolderJpaEntity extends NodeJpaEntity {

    public FolderJpaEntity(
        Long id,
        BookJpaEntity book,
        String title,
        @Nullable FolderJpaEntity parentNode
    ) {
        super(id, book, title, parentNode);
    }
}
