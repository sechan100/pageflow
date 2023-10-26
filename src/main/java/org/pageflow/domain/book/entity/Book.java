package org.pageflow.domain.book.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.user.entity.Account;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)//접근 수준. 상속관계에 있는 클래스에서만 생성자에 접근 가능
public class Book extends BaseEntity {

    private String title;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
    private List<Page> pages;

    @ManyToOne
    private Account account;
}
