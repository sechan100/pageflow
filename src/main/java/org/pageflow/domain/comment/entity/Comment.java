package org.pageflow.domain.comment.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.user.entity.Account;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Account author;

    @ManyToMany
    Set<Account> voter;


}
