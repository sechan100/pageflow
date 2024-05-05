package org.pageflow.boundedcontext.book.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.boundedcontext.book.application.dto.TocDto;
import org.pageflow.boundedcontext.book.domain.INode;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.book.domain.aggregateroot.Folder;
import org.pageflow.boundedcontext.book.domain.aggregateroot.NodeEntity;
import org.pageflow.boundedcontext.book.domain.aggregateroot.Page;
import org.pageflow.boundedcontext.book.port.in.CreateFolderCmd;
import org.pageflow.boundedcontext.book.port.in.CreatePageCmd;
import org.pageflow.boundedcontext.book.port.in.ReparentCmd;
import org.pageflow.boundedcontext.book.port.in.TocUseCase;
import org.pageflow.boundedcontext.book.port.out.NodePersistencePort;
import org.pageflow.global.api.code.Code3;
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
    private final NodePersistencePort nodePersist;


    @Override
    public TocDto.Node createFolder(CreateFolderCmd cmd) {
        Folder f = Folder.create(cmd.getTitle());
        nodePersist.saveNode(f);
        return toDto(f);
    }

    @Override
    public TocDto.Node createPage(CreatePageCmd cmd) {
        Page p = Page.create(cmd.getTitle());
        nodePersist.saveNode(p);
        return toDto(p);
    }

    @Override
    public TocDto.Node changeTitle(NodeId id, Title title) {
        NodeEntity node = loadNode(id);
        node.changeTitle(title);
        nodePersist.saveNode(node);
        return toDto(node);
    }

    @Override
    public TocDto.Node reparent(ReparentCmd cmd) {
        Folder f = loadFolder(cmd.getDestFolder());
        INode node = loadNode(cmd.getNodeId());
        f.reparent(node, cmd.getDestIndex());
        nodePersist.saveNode(f);
        return toDto(f);
    }

    @Override
    public void deleteNode(NodeId id) {
        NodeEntity node = loadNode(id);
        nodePersist.deleteNode(node);
    }



    private <N extends INode> TocDto.Node toDto(N n) {
        return new TocDto.Node(
            n.getId().getValue(),
            n.getTitle().getValue()
        );
    }

    private NodeEntity loadNode(NodeId id) {
        return nodePersist.loadNode(id)
            .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("목차 노드를 찾을 수 없습니다."));
    }

    private Folder loadFolder(NodeId id) {
        return nodePersist.loadFolder(id)
            .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("폴더를 찾을 수 없습니다."));
    }

    private Page loadPage(NodeId id) {
        return nodePersist.loadPage(id)
            .orElseThrow(() -> Code3.DATA_NOT_FOUND.feedback("페이지를 찾을 수 없습니다."));
    }
}
