package org.pageflow.boundedcontext.book.command;

import org.pageflow.boundedcontext.book.constants.BookPolicy;
import org.pageflow.boundedcontext.book.domain.OutlineParentNode;
import org.pageflow.shared.infra.domain.DomainEvent;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author : sechan
 */
public abstract class AbstractAddNewChildCmd extends DomainEvent {
    private final String title;

    public AbstractAddNewChildCmd(OutlineParentNode parentOutlineNode, @Nullable String titleOrNull){
        super(parentOutlineNode);
        this.title = Objects.requireNonNull(titleOrNull, BookPolicy.DEFAULT_FOLDER_TITLE);
    }

    @Nullable
    public String getTitleOrNull(){
        return title;
    }

    public OutlineParentNode getParentNode(){
        return (OutlineParentNode) getSource();
    }
}
