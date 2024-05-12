package org.pageflow.boundedcontext.book.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.book.application.dto.TocDto;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.domain.toc.Toc;
import org.pageflow.boundedcontext.book.port.in.*;
import org.pageflow.boundedcontext.book.port.out.NodePersistencePort;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
import org.pageflow.global.flow.code.Case3;
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
    public TocDto.Node createFolder(CreateFolderCmd cmd) {
        Folder f = persistPort.createFolder(cmd);
        return toDto(f);
    }

    @Override
    public TocDto.Node createPage(CreatePageCmd cmd) {
        Page p = persistPort.createPage(cmd);
        return toDto(p);
    }

    @Override
    public TocDto.Node changeTitle(NodeId id, Title title) {
        NodeAr node = persistPort.loadNode(id).orElseThrow(()-> Case3.DATA_NOT_FOUND.feedback("노드를 찾을 수 없습니다."));
        node.changeTitle(title);
        persistPort.saveNode(node);
        return toDto(node);
    }

    @Override
    public void reparent(ReparentCmd cmd) {
        Toc toc = tocPort.loadToc(cmd.getBookId());
        toc.reparent(cmd.getFolderId(), cmd.getNodeId(), cmd.getDest());
        tocPort.saveToc(toc);
    }

    @Override
    public void reorder(ReorderCmd cmd) {
        Toc toc = tocPort.loadToc(cmd.getBookId());
        toc.reorder(cmd.getNodeId(), cmd.getDest());
        tocPort.saveToc(toc);
    }


    @Override
    public void deleteNode(NodeId id) {
        persistPort.deleteNode(id);
    }



    private <N extends NodeAr> TocDto.Node toDto(N n) {
        return new TocDto.Node(
            n.getId().getValue(),
            n.getTitle().getValue()
        );
    }
}
