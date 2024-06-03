package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.FolderJpaEntity;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.FolderJpaRepository;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeJpaEntity;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeJpaRepository;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.TocChild;
import org.pageflow.boundedcontext.book.domain.toc.TocFolder;
import org.pageflow.boundedcontext.book.domain.toc.TocPage;
import org.pageflow.boundedcontext.book.domain.toc.TocRoot;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
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
    public TocRoot loadTocRoot(BookId bookId) {
        List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId.toLong());
        List<TocChild> rootChildren = buildTree(nodeProjections);
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

    private List<TocChild> buildTree(List<NodeProjection> projections){
        Map<NodeId, TocChild> nodeMap = new HashMap<>();
        for(NodeProjection p : projections) {
            NodeId nodeId = NodeId.from(p.getId());
            TocChild node;
            if(p.getType().equals(FolderJpaEntity.class)){
                node = new TocFolder(nodeId, null, p.getOv(), null);
            } else {
                node = new TocPage(nodeId, null, p.getOv());
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
