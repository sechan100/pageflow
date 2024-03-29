package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.book.constants.BookCreatePolicy;
import org.pageflow.boundedcontext.book.constants.OutlineNodeType;
import org.pageflow.global.data.LongIdPkBaseBaseEntity;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "node_type")
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "ordinal_value"})
)
public class OutlineNode extends LongIdPkBaseBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Book book;

    @Column(name = "title", nullable = false)
    private String title;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    @SuppressWarnings("ClassReferencesSubclass")
    private Folder parentNode;

    @Column(name = "ordinal_value", nullable = false)
    private Integer ordinalValue;


    protected OutlineNode(Folder parentNode, OutlineNodeType nodeType) {
        this.book = parentNode.getBook();
        switch(nodeType) {
            case ROOT_FOLDER:
                this.title = BookCreatePolicy.ROOT_FOLDER_TITLE;
                break;
            case FOLDER:
                this.title = BookCreatePolicy.DEFAULT_FOLDER_TITLE;
                break;
            case PAGE:
                this.title = BookCreatePolicy.DEFAULT_PAGE_TITLE;
                break;
            default:
                assert false;
        }
        this.parentNode = parentNode;
        this.ordinalValue = parentNode.getLastNodeOrdinalValue() + BookCreatePolicy.ORDINAL_OFFSET;
    }

    /**
     * for create 'root folder'
     */
    protected OutlineNode(Book book) {
        this.book = book;
        this.title = BookCreatePolicy.ROOT_FOLDER_TITLE;
        this.parentNode = null;
        this.ordinalValue = 0;
    }


    public void changeTitle(String title) {
        this.title = title;
    }

    @Nullable
    public Folder getParentNodeOrNull(){
        return parentNode;
    }
}
