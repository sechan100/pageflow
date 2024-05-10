package org.pageflow.boundedcontext.book.domain.toc;

/**
 * @author : sechan
 */
public abstract class TocEvent {
    abstract boolean isOverride(TocEvent event);
}
