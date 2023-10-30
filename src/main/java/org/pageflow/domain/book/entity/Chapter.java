package org.pageflow.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.base.entity.BaseEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)//접근 수준. 상속관계에 있는 클래스에서만 생성자에 접근 가능
public class Chapter extends BaseEntity {

    private String chaptitle;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<Page> pages;
}
