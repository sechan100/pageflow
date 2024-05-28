package org.pageflow.boundedcontext.book.application.dto;

import lombok.Value;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public abstract class BookDto {

    @Value
    public static class Simple {
        TSID id;
        String title;
        String coverImageUrl;
    }


}
