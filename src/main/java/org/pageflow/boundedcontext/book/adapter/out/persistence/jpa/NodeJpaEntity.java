package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.pageflow.shared.jpa.BaseJpaEntity;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "node_type")
@Table(
    name = "node"
)
public abstract class NodeJpaEntity extends BaseJpaEntity {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "book_id", nullable = false)
    private BookJpaEntity book;

    @Column(name = "title", nullable = false)
    private String title;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id")
    private FolderJpaEntity parentNode;


    protected NodeJpaEntity(
        Long id,
        BookJpaEntity book,
        String title,
        @Nullable FolderJpaEntity parentNode
    ) {
        this.id = id;
        this.book = book;
        this.title = title;
        this.parentNode = parentNode;
    }
}
