package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.shared.data.entity.TsidBaseEntity;
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
public class ChildNodeEntity extends TsidBaseEntity {

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

    /*
    * REVIEW: childNodeEntity를 생성하기 위해서는 생성자로 Book, 또는 Folder를 전달한다.
    * 이 때, 부모타입 node의 역정규화 칼럼인 'lastNodeOrdinalValue'를 직접 조작한다.
    * 이러한 로직이 생성자에 캡슐화되어있기에,
    * 호출자는 자식을 생성했을 뿐인데 부모 엔티티에 update 쿼리가 발생한다는 사실을 알 수 없다.
    * JPA 기반에서, 쿼리 예측 가능성은 매우 중요한 문제이다.
    * */
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
