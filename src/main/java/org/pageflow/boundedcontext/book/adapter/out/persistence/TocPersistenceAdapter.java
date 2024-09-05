package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.TocChild;
import org.pageflow.boundedcontext.book.domain.toc.TocFolder;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;
import org.pageflow.boundedcontext.book.domain.toc.TocSection;
import org.pageflow.boundedcontext.book.dto.TocDto;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author : sechan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TocPersistenceAdapter implements TocPersistencePort {
    private static final String PROJECTIONS_CACHE_KEY = "NodePersistenceAdapter.TocNodeMapCache";

    private final NodeJpaRepository nodeRepo;
    private final FolderJpaRepository folderRepo;


    @Override
    public TocDto.Toc queryToc(BookId bookId) {
        List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId.toLong());
        NodeProjection.ProjectionStrategy<TocDto.Node, TocDto.Folder, TocDto.Section> strategy = new NodeProjection.ProjectionStrategy<>(
            (p, c) -> new TocDto.Folder(TSID.from(p.getId()), p.getTitle(), c),
            (p) -> new TocDto.Section(TSID.from(p.getId()), p.getTitle())
        );
        List<TocDto.Node> rootTree = buildProjectionTree(nodeProjections, strategy);
        return new TocDto.Toc(bookId.getValue(), rootTree);
    }

    @Override
    public TocRoot loadTocRoot(BookId bookId) {
        List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId.toLong());
        NodeProjection.ProjectionStrategy<TocChild, TocFolder, TocSection> strategy = new NodeProjection.ProjectionStrategy<>(
            (p, c) -> new TocFolder(NodeId.from(p.getId()), null, p.getOv(), c),
            (p) -> new TocSection(NodeId.from(p.getId()), null, p.getOv())
        );
        List<TocChild> rootChildren = buildProjectionTree(nodeProjections, strategy);
        return new TocRoot(bookId, rootChildren);
    }

    @Override
    public TocRoot saveToc(TocRoot root) {
        Set<TocChild> movedNodes = root.flushMovedChildren();
        for(TocChild node : movedNodes){
            saveNode(node);
        }
        return root;
    }

    @Transactional
    private void saveNode(TocChild node){
        NodeJpaEntity entity = nodeRepo.findById(node.getId().toLong()).get();
        entity.setOv(node.getOv());

        Long parentId = node.getParent().getId().toLong();
        FolderJpaEntity newParentRef;
        if(parentId == 0L){
            newParentRef = null;
        } else {
            newParentRef = folderRepo.getReferenceById(parentId);
        }
        entity.setParentNode(newParentRef);
    }

    private <N> List<N> buildProjectionTree(List<NodeProjection> projections, NodeProjection.ProjectionStrategy strategy){
        Map<NodeId, NodeProjection> nodeMap = new HashMap<>();
        for(NodeProjection p : projections) {
            NodeId nodeId = NodeId.from(p.getId());
            nodeMap.put(nodeId, p);
        }

        List<NodeProjection> rootChildren = new LinkedList<>();
        for(NodeProjection p : projections){
            NodeId nodeId = NodeId.from(p.getId());
            NodeId parentId = p.getParentId() != null ? NodeId.from(p.getParentId()) : null;
            if (parentId == null) {
                rootChildren.add(nodeMap.get(nodeId));
            } else {
                NodeProjection parentNode = nodeMap.get(parentId);
                parentNode.addAccordingToOv(nodeMap.get(nodeId));
            }
        }
        return new NodeProjection.Root(rootChildren).projectTree(strategy);
    }

    @Deprecated
    private List<TocChild> buildTree(List<NodeProjection> projections){
        Map<NodeId, TocChild> nodeMap = new HashMap<>();
        for(NodeProjection p : projections) {
            NodeId nodeId = NodeId.from(p.getId());
            TocChild node;
            if(p.getType().equals(FolderJpaEntity.class)){
                node = new TocFolder(nodeId, null, p.getOv(), null);
            } else {
                node = new TocSection(nodeId, null, p.getOv());
            }
            nodeMap.put(nodeId, node);
        }

        List<TocChild> rootChildren = new LinkedList<>();
        for(NodeProjection p : projections){
            NodeId nodeId = NodeId.from(p.getId());
            NodeId parentId = p.getParentId() != null ? NodeId.from(p.getParentId()) : null;
            if (parentId == null) {
                rootChildren.add(nodeMap.get(nodeId));
                // child node
            } else {
                TocFolder parentNode = (TocFolder) nodeMap.get(parentId);
                parentNode._addAccordingToOv(nodeMap.get(nodeId));
            }
        }
        return rootChildren;
    }
}
