package org.pageflow.boundedcontext.book.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.domain.toc.TocChild;
import org.pageflow.boundedcontext.book.domain.toc.TocNode;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;
import org.pageflow.boundedcontext.book.dto.TocDto;
import org.pageflow.boundedcontext.book.port.in.*;
import org.pageflow.boundedcontext.book.port.out.NodePersistencePort;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
import org.pageflow.boundedcontext.book.shared.TocNodeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TocService implements TocUseCase {
    private final NodePersistencePort persistPort;
    private final TocPersistencePort tocPort;


    @Override
    public TocDto.Node createFolder(FolderCreateCmd cmd) {
        Folder f = persistPort.createFolder(cmd);
        return toDto(f);
    }

    @Override
    public TocDto.Node createSection(SectionCreateCmd cmd) {
        Section p = persistPort.createSection(cmd);
        return toDto(p);
    }

    @Override
    public void reparent(ReparentCmd cmd) {
        TocRoot root = tocPort.loadTocRoot(cmd.getBookId());
        TocNode node = root.findNode(cmd.getNodeId());
        if(!(node instanceof TocChild target)) throw new IllegalArgumentException("TocChild를 구현하지 않는 노드는 reparent의 대상이 될 수 없습니다.");
        // reparent
        target.getParent().reparent(cmd.getDestOrder(), target);
        tocPort.saveToc(root);
    }

    @Override
    public void reorder(ReorderCmd cmd) {
        TocRoot root = tocPort.loadTocRoot(cmd.getBookId());
        TocNode node = root.findNode(cmd.getNodeId());
        if(!(node instanceof TocChild target)) throw new IllegalArgumentException("TocChild를 구현하지 않는 노드는 reorder의 대상이 될 수 없습니다.");
        // reorder
        target.getParent().reorder(cmd.getDestOrder(), target);
        tocPort.saveToc(root);
    }


    @Override
    public void deleteNode(NodeId id) {
        persistPort.deleteNode(id);
    }

    @Override
    public TocDto.Toc queryToc(BookId bookId) {
        return tocPort.queryToc(bookId);
    }


    private <N extends AbstractNode> TocDto.Node toDto(N n) {
        TocNodeType type = n instanceof Folder ? TocNodeType.FOLDER : TocNodeType.SECTION;
        return new TocDto.Node(
            n.getId().getValue(),
            n.getTitle().getValue(),
            type
        );
    }
}
