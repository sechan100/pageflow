package org.pageflow.boundedcontext.book.dto;

import lombok.Value;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
@Value
public class AuthorDto {
    TSID id;
    String penname;
    String profileImageUrl;
}
