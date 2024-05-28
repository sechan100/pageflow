package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaEntity;
import org.pageflow.shared.jpa.BaseJpaEntity;

/**
 * @author : sechan
 */
@Data
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "book")
public class BookJpaEntity extends BaseJpaEntity {

    @Id
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    @Setter(AccessLevel.NONE)
    private ProfileJpaEntity author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String coverImageUrl;


}
