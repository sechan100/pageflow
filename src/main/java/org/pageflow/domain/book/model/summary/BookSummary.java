package org.pageflow.domain.book.model.summary;

import lombok.Data;
import org.pageflow.domain.book.constants.BookStatus;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.user.model.dto.UserSession;

import java.time.LocalDateTime;

/**
 * @author : sechan
 */
@Data
public class BookSummary {
    
    private Long id;
    
    private String title;
    
    private String coverImgUrl;
    
    private UserSession author;
    
    private BookStatus status;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime modifiedDate;
    
    
    
    public BookSummary(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.coverImgUrl = book.getCoverImgUrl();
        this.author = new UserSession(book.getAuthor());
        this.createdDate = book.getCreatedDate();
        this.modifiedDate = book.getModifiedDate();
        this.status = book.getStatus();
    }
}
