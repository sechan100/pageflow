package org.pageflow.boundedcontext.book.adapter.in.web;

import lombok.Data;

/**
 * @author : sechan
 */
public abstract class Req {
    @Data
    public static class CreateBook {
        private String title;
        private String coverImageUrl;
    }
}
