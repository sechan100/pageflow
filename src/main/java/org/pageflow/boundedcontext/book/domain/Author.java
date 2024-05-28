package org.pageflow.boundedcontext.book.domain;

import lombok.Getter;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.domain.Penname;

/**
 * @author : sechan
 */
@Getter
public class Author {
    private final UID id;
    private final Penname penname;

    public Author(UID id, Penname penname) {
        this.id = id;
        this.penname = penname;
    }
}
