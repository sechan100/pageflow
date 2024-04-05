package org.pageflow.boundedcontext.book.command;

import org.pageflow.boundedcontext.book.model.OutlineParentNode;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
public class AddNewPageCmd extends AbstractAddNewChildCmd {
    public AddNewPageCmd(OutlineParentNode parentOutlineNode, @Nullable String titleOrNull){
        super(parentOutlineNode, titleOrNull);
    }
}
