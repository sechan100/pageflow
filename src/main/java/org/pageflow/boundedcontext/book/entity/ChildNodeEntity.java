package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.global.data.LongIdPkBaseBaseEntity;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author : sechan
 */
@Entity
@Getter
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "node_type")
@Table(
    name = "child_node",
    uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "ordinal_value"})
)
public class ChildNodeEntity extends LongIdPkBaseBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Nullable
    @Setter(onMethod_ = @Nullable)
    @Getter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.MERGE)
    @JoinColumn(name = "parent_id")
    @SuppressWarnings("ClassReferencesSubclass")
    private FolderEntity parentNode;

    @Setter
    @Column(name = "ordinal_value", nullable = false)
    private Integer ordinalValue;

    protected ChildNodeEntity(ParentNodeEntity parent, String title){
        this.book = parent.getBook();
        this.title = title;

        if(parent instanceof FolderEntity parentFolderEntity){
            this.parentNode = parentFolderEntity;
        } else if(parent instanceof BookEntity){
            this.parentNode = null;
        } else {
            assert false;
        }

        this.ordinalValue = parent.increaseLastNodeOrdinalValueByOffsetAndGet();
    }

    public ParentNodeEntity getParentNode(){
        return Objects.requireNonNullElse(parentNode, this.book);
    }
}
