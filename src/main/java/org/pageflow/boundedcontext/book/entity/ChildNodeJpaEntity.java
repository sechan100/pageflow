//package org.pageflow.boundedcontext.book.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.pageflow.shared.jpa.BaseJpaEntity;
//import org.springframework.lang.Nullable;
//
///**
// * @author : sechan
// */
//@Entity
//@Getter
//@Setter(AccessLevel.NONE)
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@EqualsAndHashCode(callSuper = true)
//@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "node_type")
//@Table(
//    name = "child_node",
//    uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "ordinal_value"})
//)
//public class ChildNodeJpaEntity extends BaseJpaEntity {
//
//    @Id
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
//    @JoinColumn(name = "book_id", nullable = false)
//    private BookJpaEntity book;
//
//    @Setter
//    @Column(name = "title", nullable = false)
//    private String title;
//
//    @Nullable
//    @Setter(onMethod_ = @Nullable)
//    @Getter(AccessLevel.NONE)
//    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.MERGE)
//    @JoinColumn(name = "parent_id")
//    @SuppressWarnings("ClassReferencesSubclass")
//    private FolderJpaEntity parentNode;
//
//    @Setter
//    @Column(name = "ordinal_value", nullable = false)
//    private Integer ordinalValue;
//
//}
