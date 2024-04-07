package org.pageflow.boundedcontext.book.domain;

import lombok.Getter;
import org.pageflow.boundedcontext.book.command.AddNewFolderCmd;
import org.pageflow.boundedcontext.book.command.AddNewPageCmd;
import org.pageflow.shared.infra.domain.AggregateRoot;
import org.pageflow.shared.type.TSID;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : sechan
 */
public abstract class OutlineParentNode extends AggregateRoot<TSID> implements OutlineNode {

    @Getter
    private String title;

    private TSID bookId; // lazy

    protected List<OutlineNode> children;


    public OutlineParentNode(TSID id){
        super(id);
        children = new LinkedList<>();
    }


    public List<OutlineNode> getChildren(){
        return Collections.unmodifiableList(children);
    }

    /**
     * 해당 node 하위에 마지막 순서로 새로운 폴더를 생성한다.
     * @param titleOrNull 폴더 제목: null이라면 기본값을 사용
     */
    public void addNewFolder(String titleOrNull){
        raiseEvent(new AddNewFolderCmd(this, titleOrNull));
    }

    public void addNewPage(String titleOrNull){
        raiseEvent(new AddNewPageCmd(this, titleOrNull));
    }
}
