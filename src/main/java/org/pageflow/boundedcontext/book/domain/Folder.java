package org.pageflow.boundedcontext.book.domain;


import lombok.Getter;
import org.pageflow.boundedcontext.common.annotation.AggregateRoot;
import org.springframework.util.Assert;

/**
 * @author : sechan
 */
@Getter
@AggregateRoot
public class Folder implements NodeAr {
    private final BookId bookId;
    /**
     * {@code Folder}는 RootFolder가 아닌 모든 폴더를 모델링한다. 따라서 parentId가 null인 경우는 없다.
     */
    private final NodeId parentId;
    private final NodeId id;
    private Title title;


    public Folder(BookId bookId, NodeId parentId, NodeId id, Title title) {
        Assert.notNull(parentId, "해당 모델은 parentId가 null인 root Folder를 지원하지 않습니다.");
        this.bookId = bookId;
        this.parentId = parentId;
        this.id = id;
        this.title = title;
    }


    public void changeTitle(Title title) {
        this.title = title;
    }
}
