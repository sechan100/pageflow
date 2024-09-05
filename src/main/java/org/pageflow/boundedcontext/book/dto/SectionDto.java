package org.pageflow.boundedcontext.book.dto;

import lombok.Value;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public abstract class SectionDto {
    @Value
    public static class WithContent {
        TSID id;
        String title;
        String content;
    }

    @Value
    public static class MetaData {
        TSID id;
        String title;
    }
}
