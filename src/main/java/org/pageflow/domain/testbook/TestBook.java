package org.pageflow.domain.testbook;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pageflow.domain.user.entity.Account;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Builder
public class TestBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 200)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createDate;
    @ManyToOne
    private Account author;
    private String coverImgUrl;
}
