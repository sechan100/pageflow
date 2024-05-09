package org.pageflow.boundedcontext.book.port.in;

import lombok.Getter;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class CreateFolderCmd {
    private static final Title DEFAULT_TITLE = new Title("새 폴더");

    private final BookId bookId;
    private final NodeId parentNodeId;
    private final Title title;

    public CreateFolderCmd(
        BookId bookId,
        NodeId parentNodeId,
        @Nullable Title title
    ) {
        this.bookId = bookId;
        this.parentNodeId = parentNodeId;
        this.title = title == null ? DEFAULT_TITLE : title;
    }

}
