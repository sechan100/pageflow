package org.pageflow.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.book.constants.BookStatus;
import org.pageflow.domain.user.entity.Profile;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    private BookStatus status;
    
    @Column(nullable = true)
    private LocalDateTime publishedDate;


    // AllArgsConstructor: chapters 값을 초기화하기위해서 하드코딩
    protected Book(String title, String coverImgUrl, Profile author, List<Chapter> chapters, BookStatus status, LocalDateTime publishedDate) {
        super();
        this.title = title;
        this.coverImgUrl = coverImgUrl;
        this.author = author;
        this.chapters = Objects.requireNonNullElseGet(chapters, ArrayList::new);
        this.status = status;
        this.publishedDate = publishedDate;
    }


}