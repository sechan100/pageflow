package org.pageflow.boundedcontext.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.TocChild;
import org.pageflow.boundedcontext.book.domain.toc.TocParent;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;
import org.pageflow.boundedcontext.book.port.in.*;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.global.dev.user.TestUserCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author : sechan
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TocUseCaseTest {
    @Autowired
    private TestUserCreator userCreator;
    @Autowired
    private BookUseCase bookUseCase;
    @Autowired
    private TocUseCase tocUseCase;
    @Autowired
    private TocPersistencePort tocPort;

    private UID _uid;
    private BookId _bookId;

    @BeforeEach
    void createBook() {
        this._uid = userCreator.create("tuser");
        CreateBookCmd cmd = new CreateBookCmd(
            _uid,
            "테스트용 책",
            "/book/test/cover/image.jpg"
        );
        _bookId = BookId.from(bookUseCase.createBook(cmd).getId());
    }

    @Test
    @Transactional
    void createTocNodes() {
        NodeId f1 = cf(NodeId.ROOT);
            NodeId f2 = cf(f1);
                NodeId p1 = cp(f2);
        NodeId p2 = cp(NodeId.ROOT);

        TocRoot root = tocPort.loadTocRoot(_bookId);
        TocChild folder1 = root.getChild(f1);
        TocChild folder2 = ((TocParent) folder1).getChild(f2);
        TocChild page1 = ((TocParent) folder2).getChild(p1);
        TocChild page2 = root.getChild(p2);

        assertEquals(f1, folder1.getId(), "f1의 id가 일치하지 않습니다.");
        assertEquals(f2, folder2.getId(), "f2의 id가 일치하지 않습니다.");
        assertEquals(p1, page1.getId(), "p1의 id가 일치하지 않습니다.");
        assertEquals(p2, page2.getId(), "p2의 id가 일치하지 않습니다.");
    }





    private NodeId cf(NodeId parentNodeId){
        CreateFolderCmd cmd = new CreateFolderCmd(
            _bookId,
            parentNodeId
        );
        return NodeId.from(tocUseCase.createFolder(cmd).getId());
    }

    private NodeId cp(NodeId parentNodeId){
        CreatePageCmd cmd = new CreatePageCmd(
            _bookId,
            parentNodeId
        );
        return NodeId.from(tocUseCase.createPage(cmd).getId());
    }
}