package org.pageflow.boundedcontext.book.domain;

/**
 * @author : sechan
 */
public class Book {
    private final BookId id;
    private Title title;
    private CoverImageUrl coverImageUrl;


    private Book(BookId id, Title title, CoverImageUrl coverImageUrl) {
        this.id = id;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
    }


    public Book create(Title title, CoverImageUrl coverImageUrl){
        return new Book(BookId.random(), title, coverImageUrl);
    }

    public void changeTitle(Title title){
        this.title = title;
    }

    public void changeCoverImageUrl(CoverImageUrl coverImageUrl){
        this.coverImageUrl = coverImageUrl;
    }












}
