package org.pageflow.boundedcontext.book.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.pageflow.boundedcontext.book.shared.TocNodeType;
import org.pageflow.shared.type.TSID;

import java.util.ArrayList;
import java.util.List;

public abstract class TocDto {

    @Getter
    @RequiredArgsConstructor
    public static class Node {
        private final TSID id;
        private final String title;
        private final TocNodeType type;
    }

    @Getter
    public static class Folder extends Node {
        private final List<Node> children;

        public Folder(TSID id, String title, List<Node> children) {
            super(id, title, TocNodeType.FOLDER);
            this.children = new ArrayList<>(children);
        }

    }

    public static class Section extends Node {
        public Section(TSID id, String title) {
            super(id, title, TocNodeType.SECTION);
        }
    }

    @Value
    public static class Toc {
        TSID bookId;
        Folder root;
    }
}