package org.pageflow.boundedcontext.book.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.entity.Book;
import org.pageflow.boundedcontext.book.entity.Folder;
import org.pageflow.boundedcontext.book.entity.OutlineNode;
import org.pageflow.boundedcontext.book.repository.BookRepo;
import org.pageflow.boundedcontext.book.repository.FolderRepo;
import org.pageflow.boundedcontext.book.repository.OutlineNodeRepo;
import org.pageflow.boundedcontext.book.repository.PageRepo;
import org.pageflow.boundedcontext.user.repository.ProfileRepo;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : sechan
 */
@Service
@RequiredArgsConstructor
public class DummyBookCreator {
    private final BookRepo bookRepo;
    private final OutlineNodeRepo outlineNodeRepo;
    private final FolderRepo folderRepo;
    private final PageRepo pageRepo;
    private final ProfileRepo profileRepo;

    private static final int ORDINAL_OFFSET = 10000;

    @Getter
    private static class Dummy {
        private final Book book;
        private final List<OutlineNode> nodes;
        private final Map<OutlineNode, Integer> maxOrdinalValues;

        private Dummy(Book book){
            this.book = book;
            this.nodes = new LinkedList<>();
            this.maxOrdinalValues = new HashMap<>();
        }

        public int getNextOrdinalValue(Folder node){
            int maxOrdinalValue = Objects.requireNonNullElse(
                maxOrdinalValues.get(node),
                ORDINAL_OFFSET
            );
            Integer newMaxOrdinalValue = maxOrdinalValues.replace(node, maxOrdinalValue + ORDINAL_OFFSET);
            return Objects.requireNonNullElse(newMaxOrdinalValue, 0);
        }
    }




    public void createDummy() {
    }

    private Folder createFolder(Dummy dummy, Folder parent){
        return parent;
    }

}
