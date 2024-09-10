package org.pageflow.boundedcontext.book.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.NodeRegistry;
import org.pageflow.boundedcontext.book.domain.toc.TocNode;
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
        List<TocDto.Node> root = buildTree(nodeProjections);
        return new TocDto.Toc(bookId.getValue(), root);
    }

    @Override
    public NodeRegistry loadRegistry(BookId bookId) {
        List<NodeProjection> nodeProjections = nodeRepo.queryNodesByBookId(bookId.toLong());
        Collection<TocNode> nodes = nodeProjections
            .stream()
            .map(this::mapDomain)
            .toList();
        return new NodeRegistry(bookId, nodes);
    }

    @Override
    public NodeRegistry saveNodes(NodeRegistry registry) {
        Set<TocNode> domainNodes = registry.getChangedNodes();
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
        for(TocNode node : domainNodes){
            NodeJpaEntity entity = entities.get(node.getId().toLong());

            // ov 변경
            entity.setOv(node.getOv());

            // parent 변경
            Long parentId = node.getParentId().toLong();
            FolderJpaEntity newParentRef;
            if(parentId == 0L){
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



    private TocNode mapDomain(NodeProjection p){
        return new TocNode(
            NodeId.from(p.getId()),
            mapsParentId(p.getParentId()),
            p.getOv(),
            p.getType().equals(FolderJpaEntity.class) ? TocNodeType.FOLDER : TocNodeType.SECTION
        );
    }

    /**
     * DB 칼럼에 실제로 저장되는 parentId 값을, 도메인에서 의미있는 값으로 변환해주는 메서드.
     * 실제로 root 레벨에 위치한 노드들의 parentId 값은 null이지만, 도메인 안에서는 TSID.from(0L)에 해당하는 값으로 사용
     */
    private NodeId mapsParentId(Long parentIdOrNull){
        return parentIdOrNull != null ? NodeId.from(parentIdOrNull) : NodeId.ROOT;
    }

    /**
     * NodeProjection로 트리를 구성하고 TocDto.Node 기반의 트리로 변환하여 그 root를 반환한다.
     */
    private List<TocDto.Node> buildTree(List<NodeProjection> projections){
        // 트리 구성
        Map<Long, NodeProjection> nodeMap = projections
            .stream()
            .collect(Collectors.toMap(
                NodeProjection::getId,
                p -> p
            ));
        List<NodeProjection> projectionRoot = new ArrayList<>();
        for(NodeProjection p : projections){
            Long parentId = p.getParentId();
            if(parentId== null){ // root
                projectionRoot.add(p);
            } else {
                NodeProjection parent = nodeMap.get(parentId);
                parent.addChildAccordingToOv(p);
            }
        }
        // NodeProjection 객체를 Dto 객체로 변환
        List<TocDto.Node> root = projectionRoot.stream()
            .map(this::projectRecursively)
            .toList();
        return root;
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
