package org.pageflow.boundedcontext.book.domain.toc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : sechan
 */
public class MovedChildrenSet {
    private Set<TocChild> moved;


    public MovedChildrenSet() {
        this.moved = new HashSet<>();
    }

    public void add(TocChild child) {
        moved.add(child);
    }

    public Set<TocChild> toSet() {
        return Collections.unmodifiableSet(moved);
    }

    public Set<TocChild> flush() {
        Set<TocChild> replica = Collections.unmodifiableSet(this.moved);
        this.moved = new HashSet<>();
        return replica;
    }

    public void clear() {
        moved.clear();
    }
}
