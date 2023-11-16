package org.pageflow.domain.book.model.request;

import lombok.Data;
import org.pageflow.domain.book.entity.Book;

@Data
public class BookUpdateRequest {

    private Long id;

    private String title;

    private String coverImgUrl;

    private boolean isPublished;


    public BookUpdateRequest(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.coverImgUrl = book.getCoverImgUrl();
        this.isPublished = book.isPublished();
    }
}
