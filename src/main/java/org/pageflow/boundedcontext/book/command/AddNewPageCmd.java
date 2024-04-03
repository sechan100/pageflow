package org.pageflow.boundedcontext.book.command;

import org.springframework.context.ApplicationEvent;

/**
 * @author : sechan
 */
public class AddNewPageCmd extends ApplicationEvent {

    public AddNewPageCmd(Object source){
        super(source);
    }
}
