package org.pageflow.boundedcontext.book.port.in;


import lombok.Getter;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.shared.type.TSID;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class SectionCreateCmd {
    private static final Title DEFAULT_TITLE = Title.from("새 섹션");
    private static final String DEFAULT_CONTENT = "내용을 입력해주세요.";

    private final BookId bookId;
    private final NodeId parentNodeId;
    private final Title title;
    private final String content;


    public SectionCreateCmd(TSID bookId, TSID parentNodeId) {
        this(bookId, parentNodeId, null);
    }

    public SectionCreateCmd(
        TSID bookId,
        TSID parentNodeId,
        @Nullable Title title
    ) {
        this.bookId = BookId.from(bookId);
        this.parentNodeId = NodeId.from(parentNodeId);
        this.title = title == null ? DEFAULT_TITLE : title;
        this.content = DEFAULT_CONTENT;
    }

}
