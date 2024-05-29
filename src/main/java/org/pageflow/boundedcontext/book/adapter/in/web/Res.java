package org.pageflow.boundedcontext.book.adapter.in.web;

import lombok.Value;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public abstract class Res {
    @Value
    public static class CreatedBook {
        TSID id;
        String title;
        String coverImageUrl;
    }
}
