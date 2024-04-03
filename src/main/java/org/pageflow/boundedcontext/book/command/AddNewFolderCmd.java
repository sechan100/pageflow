package org.pageflow.boundedcontext.book.command;

import org.pageflow.boundedcontext.book.constants.BookPolicy;
import org.pageflow.boundedcontext.book.model.OutlineParentNode;
import org.pageflow.infra.domain.DomainEvent;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author : sechan
 */
public class AddNewFolderCmd extends DomainEvent {
    private final String title;

    public AddNewFolderCmd(OutlineParentNode parentOutlineNode, @Nullable String titleOrNull){
        super(parentOutlineNode);
        this.title = Objects.requireNonNull(titleOrNull, BookPolicy.DEFAULT_FOLDER_TITLE);
    }

    public String getTitle(){
        return title;
    }

    public OutlineParentNode getParentNode(){
        return (OutlineParentNode) getSource();
    }

}
