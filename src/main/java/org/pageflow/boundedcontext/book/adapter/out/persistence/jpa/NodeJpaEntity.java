package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
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
    name = "node",
    uniqueConstraints = {
        // 형제 노드끼리의 순서를 결정하기 위해서는 ov값이 형제들 사이에서 모두 unique해야 한다.
        @UniqueConstraint(name = "node_book_id_title_uk", columnNames = {"parent_id", "ov"})
    }
)
public abstract class NodeJpaEntity extends BaseJpaEntity {

    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "book_id", nullable = false)
    private BookJpaEntity book;

    @Column(name = "title", nullable = false)
    private String title;

    /**
     * 부모 노드
     * null인 경우 해당 book의 root node임을 나타낸다.
     * root 노드는 어떠한 변경에 대해서도 폐쇄적이다.
     */
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true)
    private FolderJpaEntity parentNode;

    @Column(nullable = false)
    private Integer ov;


    protected NodeJpaEntity(
        Long id,
        BookJpaEntity book,
        String title,
        FolderJpaEntity parentNode,
        int ov
    ) {
        this.id = id;
        this.book = book;
        this.title = title;
        this.parentNode = parentNode;
        this.ov = ov;
    }
}
