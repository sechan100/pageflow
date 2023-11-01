package org.pageflow.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.user.entity.Account;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)//접근 수준. 상속관계에 있는 클래스에서만 생성자에 접근 가능
public class Book extends BaseEntity {

    private String title;

    private String coverImgUrl;

    @ManyToOne
    private Account author;
    @ManyToMany
    Set<Account> voters;
}
