package org.pageflow.boundedcontext.book.dto;

import lombok.Value;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public abstract class BookDto {

    @Value
    public static class Basic {
        TSID id;
        String title;
        String coverImageUrl;
    }

    @Value
    public static class WithAuthor {
        TSID id;
        String title;
        String coverImageUrl;
        AuthorDto author;
    }


}
