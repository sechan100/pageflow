package org.pageflow.boundedcontext.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pageflow.boundedcontext.book.constants.BookCreatePolicy;
import org.pageflow.boundedcontext.user.entity.Profile;
import org.pageflow.global.data.LongIdPkBaseBaseEntity;

/**
 * @author : sechan
 */
@Entity
@Setter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Table
public class Book extends LongIdPkBaseBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    @Getter
    private Profile author;

    @Column(nullable = false)
    @Getter
    private String title;

    @Getter
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "root_folder_id")
    private Folder rootFolder;

    private Book(Profile author){
        this.author = author;
        this.title = BookCreatePolicy.DEFAULT_BOOK_TITLE;
        this.rootFolder = null;
    }

    public static Book create(Profile author){
        Book book = new Book(author);
        book.rootFolder = Folder.createRootFolder(book);
        return book;
    }
}
