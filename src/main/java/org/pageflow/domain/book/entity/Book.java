package org.pageflow.domain.book.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.user.entity.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //접근 수준. 상속관계에 있는 클래스에서만 생성자에 접근 가능
@DynamicUpdate
public class Book extends BaseEntity {

    private String title;

    private String coverImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile author;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "book")
    private List<Chapter> chapters = new ArrayList<>();

    /**
     * 출판 여부
     * 출판 신청된 책을 검수를 통해서 출판한다. 출판 허가된 책은 공개된다.
     * 책을 수정하기 위해서는 출판을 취소후, 수정후에 다시 출판 신청을 해야한다.
     */
    private boolean isPublished;


    // AllArgsConstructor: chapters 값을 초기화하기위해서 하드코딩
    protected Book(String title, String coverImgUrl, Profile author, List<Chapter> chapters, boolean isPublished) {
        super();
        this.title = title;
        this.coverImgUrl = coverImgUrl;
        this.author = author;
        this.chapters = Objects.requireNonNullElseGet(chapters, ArrayList::new);
        this.isPublished = isPublished;
    }


}