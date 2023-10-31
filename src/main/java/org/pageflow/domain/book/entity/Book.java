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

    private String imgUrl;

    @ManyToOne
    private Account author;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
    private List<Chapter> chapters;

    @ManyToMany
    Set<Account> voters;

    @Column(columnDefinition = "integer default 0", nullable = false) //조회 수 기본값 0, null 불가 처리
    private int view;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int voterCount; // 추천 수를 저장

    public void addVoter(Account voter) {
        this.voters.add(voter);
        this.voterCount = this.voters.size(); // 추천할 때마다 업데이트
    }

    public void removeVoter(Account voter) {
        this.voters.remove(voter);
        this.voterCount = this.voters.size(); // 추천 취소할 때마다 업데이트
    }
}
