package org.pageflow.boundedcontext.book.application.dto;

import lombok.Value;
import org.pageflow.shared.type.TSID;

public abstract class TocDto {

    @Value
    public static class Node {
        TSID id;
        String title;
    }
}