package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.ChildableTocNode;
import org.pageflow.boundedcontext.book.domain.toc.NodeRegistry;
import org.pageflow.boundedcontext.book.domain.toc.TocRootNode;
import org.pageflow.boundedcontext.book.dto.TocDto;
import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
import org.pageflow.boundedcontext.book.shared.TocNodeType;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TocPersistenceAdapter implements TocPersistencePort {
    private final NodeJpaRepository nodeRepo;
    private final FolderJpaRepository folderRepo;


    @Override
    public TocDto.Toc queryToc(BookId bookId) {
        List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId.toLong());
        TocDto.Folder root = buildTree(nodeProjections);
        return new TocDto.Toc(bookId.getValue(), root);
    }

    @Override
    public NodeRegistry loadRegistry(BookId bookId) {
        List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId.toLong());

        TocRootNode root = null;
        Collection<ChildableTocNode> nodes = new ArrayList<>();
        for(NodeProjection p : nodeProjections){
            if(p.getParentId()==null){
                root = new TocRootNode(NodeId.from(p.getId()), TocNodeType.FOLDER);
            } else {
                nodes.add(
                    new ChildableTocNode(
                        NodeId.from(p.getId()),
                        NodeId.from(p.getParentId()),
                        p.getOv(),
                        p.getType().equals(FolderJpaEntity.class) ? TocNodeType.FOLDER:TocNodeType.SECTION
                    )
                );
            }
        }
        if(root==null) throw new IllegalStateException("루트 노드가 없습니다.");
        return new NodeRegistry(bookId, root, nodes);
    }

    @Override
    public NodeRegistry saveNodes(NodeRegistry registry) {
        Set<ChildableTocNode> domainNodes = registry.getChangedNodes();
        Map<Long, NodeJpaEntity> entities = nodeRepo.findAllById(
                domainNodes
                    .stream()
                    .map(changed -> changed.getId().toLong())
                    .collect(Collectors.toSet())
            )
            .stream()
            .collect(Collectors.toMap(
                NodeJpaEntity::getId,
                entity -> entity
            ));
        for(ChildableTocNode node : domainNodes){
            NodeJpaEntity entity = entities.get(node.getId().toLong());

            // ov 변경
            entity.setOv(node.getOv());

            // parent 변경
            Long parentId = node.getParentId().toLong();
            FolderJpaEntity newParentRef;
            if(parentId==0L){
                newParentRef = null;
            } else {
                newParentRef = folderRepo.getReferenceById(parentId);
            }
            entity.setParentNode(newParentRef);

            // changed flag 초기화
            node.new IsChangedResetter().reset();
        }
        return registry;
    }

    @Override
    public NodeId getRootNodeId(BookId bookId) {
        FolderJpaEntity rootNode = nodeRepo.findRootNode(bookId.toLong()).orElseThrow();
        return NodeId.from(rootNode.getId());
    }

    /**
     * parentNode들의 자식중에서 가장 큰 ov 값을 찾는다.
     */
    @Override
    public Optional<Integer> loadMaxOvAmongSiblings(BookId bookId, NodeId parentNodeId) {
        return nodeRepo.findMaxOvAmongSiblings(bookId.toLong(), parentNodeId.toLong());
    }


    /**
     * NodeProjection로 트리를 구성하고 TocDto.Node 기반의 트리로 변환하여 그 root를 반환한다.
     */
    private TocDto.Folder buildTree(List<NodeProjection> projections){
        // 트리 구성
        Map<Long, NodeProjection> nodeMap = projections
            .stream()
            .collect(Collectors.toMap(
                NodeProjection::getId,
                p -> p
            ));
        NodeProjection rootProjection = projections.stream()
            .filter(p -> p.getParentId() == null)
            .findFirst()
            .orElseThrow();
        for(NodeProjection p : projections){
            Long parentId = p.getParentId();
            if(parentId == null) continue; // root
            NodeProjection parent = nodeMap.get(parentId);
            parent.addChildAccordingToOv(p);
        }

        // NodeProjection 객체를 Dto 객체로 변환
        List<TocDto.Node> rootChildren = rootProjection.getChildren().stream()
            .map(this::projectRecursively)
            .toList();

        return new TocDto.Folder(
            new TSID(rootProjection.getId()),
            rootProjection.getTitle(),
            rootChildren
        );
    }

    private TocDto.Node projectRecursively(NodeProjection projection){
        if(projection.getType().equals(FolderJpaEntity.class)){
            List<TocDto.Node> children;
            if(projection.getChildren() != null){
                children = projection.getChildren().stream()
                    .map(c -> this.projectRecursively(c))
                    .toList();
            } else {
                children = Collections.emptyList();
            }
            return new TocDto.Folder(
                new TSID(projection.getId()),
                projection.getTitle(),
                children
            );
        } else {
            return new TocDto.Section(
                new TSID(projection.getId()),
                projection.getTitle()
            );
        }
    }

}
