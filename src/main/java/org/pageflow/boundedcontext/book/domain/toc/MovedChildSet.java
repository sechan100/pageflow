package org.pageflow.boundedcontext.book.domain.toc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : sechan
 */
public class MovedChildSet {
    private final Set<TocChild> moved;


    public MovedChildSet() {
        this.moved = new HashSet<>();
    }

    public void add(TocChild child) {
        moved.add(child);
    }

    public Set<TocChild> getMoved() {
        return Collections.unmodifiableSet(moved);
    }

    public void clear() {
        moved.clear();
    }
}
