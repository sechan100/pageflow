package org.pageflow.boundedcontext.book.domain;


import lombok.Getter;

/**
 * @author : sechan
 */
@Getter
public class Book {
    private final BookId id;
    private final Author author;
    private Title title;
    private CoverImageUrl coverImageUrl;


    public Book(
        BookId id,
        Author author,
        Title title,
        CoverImageUrl coverImageUrl
    ) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
    }



    public void changeTitle(Title title) {
        this.title = title;
    }

    public void changeCoverImageUrl(CoverImageUrl coverImageUrl){
        this.coverImageUrl = coverImageUrl;
    }

}
