package org.pageflow.domain.hic.test;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.comment.entity.Comment;
import org.pageflow.domain.user.entity.Account;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestBook extends BaseEntity {

    private String title;

    @ManyToMany
    Set<Account> voter;

    @ManyToOne
    private Account author;

    @OneToMany(mappedBy = "testBook", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;
}
