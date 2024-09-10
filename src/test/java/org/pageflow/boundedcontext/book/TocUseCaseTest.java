package org.pageflow.boundedcontext.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.TocChild;
import org.pageflow.boundedcontext.book.domain.toc.TocParent;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;
import org.pageflow.boundedcontext.book.dto.TocDto;
import org.pageflow.boundedcontext.book.port.in.*;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.shared.utility.JsonUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import support.UserCreator;

/**
 * @author : sechan
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class TocUseCaseTest {
    @Autowired
    private UserCreator userCreator;
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
        BookCreateCmd cmd = new BookCreateCmd(
            _uid.getValue(),
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

        TocRoot root = tocPort.loadRegistry(_bookId);
        TocChild folder1 = root.getChild(f1);
        TocChild folder2 = ((TocParent) folder1).getChild(f2);
        TocChild page1 = ((TocParent) folder2).getChild(p1);
        TocChild page2 = root.getChild(p2);

        assertEquals(f1, folder1.getId(), "f1의 id가 일치하지 않습니다.");
        assertEquals(f2, folder2.getId(), "f2의 id가 일치하지 않습니다.");
        assertEquals(p1, page1.getId(), "p1의 id가 일치하지 않습니다.");
        assertEquals(p2, page2.getId(), "p2의 id가 일치하지 않습니다.");
    }

    @Test
    @Transactional
    void queryToc(){
        NodeId f1 = cf(NodeId.ROOT);
            NodeId f2 = cf(f1);
                NodeId p1 = cp(f2);
                NodeId p2 = cp(f2);
            NodeId f3 = cf(f1);
                NodeId f4 = cf(f3);
                    NodeId p4 = cp(f4);
        NodeId p3 = cp(NodeId.ROOT);
        NodeId f5 = cf(NodeId.ROOT);
            NodeId p5 = cp(f5);

        TocDto.Toc toc = tocUseCase.queryToc(_bookId);
        System.out.println(JsonUtility.toJson(toc));
    }





    private NodeId cf(NodeId parentNodeId){
        FolderCreateCmd cmd = new FolderCreateCmd(
            _bookId.getValue(),
            parentNodeId.getValue()
        );
        return NodeId.from(tocUseCase.createFolder(cmd).getId());
    }

    private NodeId cp(NodeId parentNodeId){
        SectionCreateCmd cmd = new SectionCreateCmd(
            _bookId.getValue(),
            parentNodeId.getValue()
        );
        return NodeId.from(tocUseCase.createSection(cmd).getId());
    }
}