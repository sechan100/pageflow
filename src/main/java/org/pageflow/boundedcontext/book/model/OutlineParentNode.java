package org.pageflow.boundedcontext.book.model;

import lombok.Getter;
import org.pageflow.boundedcontext.book.command.AddNewFolderCmd;
import org.pageflow.infra.domain.AggregateRoot;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : sechan
 */
public abstract class OutlineParentNode extends AggregateRoot<Long> implements OutlineNode {

    @Getter
    private String title;

    private Long bookId; // lazy

    protected List<OutlineNode> children;


    public OutlineParentNode(Long id){
        super(id);
        children = new LinkedList<>();
    }


    public List<OutlineNode> getChildren(){
        return Collections.unmodifiableList(children);
    }

    /**
     * 해당 node 하위에 마지막 순서로 새로운 폴더를 생성한다.
     * @param childTitleOrNull 폴더 제목: null이라면 기본값을 사용
     */
    public void addNewFolder(String childTitleOrNull){
        raise(new AddNewFolderCmd(this, childTitleOrNull));
    }
}
