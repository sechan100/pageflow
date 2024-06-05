package org.pageflow.boundedcontext.book.application.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.pageflow.boundedcontext.book.shared.constants.TocNodeType;
import org.pageflow.shared.type.TSID;

import java.util.Collections;
import java.util.List;

public abstract class TocDto {

    @Value
    public static class SingleNode {
        TSID id;
        String title;
        TocNodeType type;
    }

    @RequiredArgsConstructor
    public static abstract class Node {
        private final TSID id;
        private final String title;
        private final TocNodeType type;
    }

    public static class Folder extends Node {
        private final List<Node> children;

        public Folder(TSID id, String title, List<Node> children) {
            super(id, title, TocNodeType.FOLDER);
            this.children = Collections.unmodifiableList(children);
        }
    }

    public static class Page extends Node {
        public Page(TSID id, String title) {
            super(id, title, TocNodeType.PAGE);
        }
    }

    @Value
    public static class Toc {
        TSID bookId;
        List<Node> children;
    }
}