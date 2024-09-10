package org.pageflow.boundedcontext.book.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.domain.toc.NodeRegistry;
import org.pageflow.boundedcontext.book.domain.toc.TocParent;
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
        int maxOvAmongSiblings = persistPort.loadMaxOvAmongSiblings(cmd.getBookId(), cmd.getParentNodeId())
            .orElse(-TocParent.OV_OFFSET);
        int ov = maxOvAmongSiblings + TocParent.OV_OFFSET;
        Folder f = persistPort.createFolder(
            cmd.getBookId(),
            cmd.getParentNodeId(),
            cmd.getTitle(),
            ov
        );
        return toDto(f);
    }

    @Override
    public TocDto.Node createSection(SectionCreateCmd cmd) {
        int maxOvAmongSiblings = persistPort.loadMaxOvAmongSiblings(cmd.getBookId(), cmd.getParentNodeId())
            .orElse(-TocParent.OV_OFFSET);
        int ov = maxOvAmongSiblings + TocParent.OV_OFFSET;
        Section s = persistPort.createSection(
            cmd.getBookId(),
            cmd.getParentNodeId(),
            cmd.getTitle(),
            cmd.getContent(),
            ov
        );
        return toDto(s);
    }

    @Override
    public void reparent(ReparentCmd cmd) {
        NodeRegistry registry = tocPort.loadRegistry(cmd.getBookId());
        TocParent parent = registry.buildTocParent(cmd.getDestfolderId());
        parent.reparent(cmd.getDestIndex(), cmd.getNodeId());
        tocPort.saveNodes(registry);
    }

    @Override
    public void reorder(ReorderCmd cmd) {
        NodeRegistry registry = tocPort.loadRegistry(cmd.getBookId());
        TocParent parent = registry.buildTocParentFromChildId(cmd.getNodeId());
        parent.reorder(cmd.getDestIndex(), cmd.getNodeId());
        tocPort.saveNodes(registry);
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
