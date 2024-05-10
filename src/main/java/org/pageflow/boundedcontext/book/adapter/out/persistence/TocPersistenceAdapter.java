package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.FolderJpaEntity;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.FolderJpaRepository;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeJpaEntity;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.NodeJpaRepository;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.*;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
import org.pageflow.shared.transaction.TransactionalCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final TransactionalCacheManager cacheManager;


    @Override
    public Toc loadToc(BookId bookId) {
        List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId.toLong());
        TocFolder root = buildTree(nodeProjections);
        cacheManager.cachify(PROJECTIONS_CACHE_KEY,
            nodeProjections.stream()
                .collect(Collectors.toMap(n -> n.getId(), n -> n)
            )
        );
        return new Toc(bookId, root);
    }

    private TocFolder buildTree(List<NodeProjection> projections){
        Map<NodeId, TocNode> nodeMap = new HashMap<>();
        for(NodeProjection p : projections) {
            NodeId nodeId = NodeId.from(p.getId());
            TocNode node;
            if(p.getType().equals(FolderJpaEntity.class)){
                node = new TocFolder(nodeId, p.getOv());
            } else {
                node = new TocPage(nodeId, p.getOv());
            }
            nodeMap.put(nodeId, node);
        }

        TocFolder root = null;
        for(NodeProjection p : projections){
            NodeId nodeId = NodeId.from(p.getId());
            NodeId parentId = p.getParentId() != null ? NodeId.from(p.getParentId()) : null;
            // root folder
            if (parentId == null) {
                root = (TocFolder) nodeMap.get(nodeId);
                // child node
            } else {
                TocFolder parentNode = (TocFolder) nodeMap.get(parentId);
                parentNode.addAccordingToOv(nodeMap.get(nodeId));
            }
        }
        return root;
    }

    @Override
    public Toc saveToc(Toc toc) {
        Map<Long, NodeProjection> projectionsIndex = (Map<Long, NodeProjection>) cacheManager.get(PROJECTIONS_CACHE_KEY);
        Assert.notNull(projectionsIndex, "캐시된 List<NodeProjection> 인스턴스가 존재하지 않습니다.");
        List<TocEvent> events = toc.getEvents();
        for(TocEvent e: events){
            // REPARENT
            if(e instanceof ReparentEvent reparentE){
                NodeJpaEntity target = nodeRepo.findById(reparentE.getReparentedNodeId().toLong()).get();
                FolderJpaEntity newParentProxy = folderRepo.getReferenceById(reparentE.getNewParentId().toLong());
                target.setParentNode(newParentProxy);
            // REORDER
            } else if(e instanceof ReorderEvent reorderE){
                TocFolder folder = (TocFolder) toc.getRoot().findNode(reorderE.getFolderId());
                folder.chidrenStream()
                    .filter(n -> n.getOv() != projectionsIndex.get(n.getId().toLong()).getOv())
                    .forEach(n -> {
                        NodeJpaEntity ovChangedNode = nodeRepo.findById(n.getId().toLong()).get();
                        ovChangedNode.setOv(n.getOv());
                    });
            }
        }
        return toc;
    }


    private TocNode toTocNode(NodeProjection p){
        if(p.getType().isAssignableFrom(FolderJpaEntity.class)){
            return new TocFolder(NodeId.from(p.getId()), p.getOv());
        } else {
            return new TocPage(NodeId.from(p.getId()), p.getOv());
        }
    }

}
