package org.pageflow.boundedcontext.book.domain;

/**
 * @author : sechan
 */
public class Book {

    private final BID bid;
    private Title title;
    private CoverImageUrl coverImageUrl;


    private Book(BID bid, Title title, CoverImageUrl coverImageUrl) {
        this.bid = bid;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
    }


    public Book create(Title title, CoverImageUrl coverImageUrl){
        return new Book(BID.random(), title, coverImageUrl);
    }

    public void changeTitle(Title title){
        this.title = title;
    }

    public void changeCoverImageUrl(CoverImageUrl coverImageUrl){
        this.coverImageUrl = coverImageUrl;
    }












}
