package org.pageflow.boundedcontext.book.dto;

import lombok.Value;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public abstract class FolderDto {
    @Value
    public static class Basic {
        TSID id;
        String title;
    }
}
