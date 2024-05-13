package org.pageflow.boundedcontext.book.adapter.out.persistence;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.port.in.CreateFolderCmd;
import org.pageflow.boundedcontext.book.port.in.CreatePageCmd;
import org.pageflow.boundedcontext.book.port.out.NodePersistencePort;
import org.pageflow.global.api.code.ApiCode3;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : sechan
 */
@Component
@Transactional
@RequiredArgsConstructor
public class NodePersistenceAdapter implements NodePersistencePort {
    private final BookJpaRepository bookRepo;
    private final NodeJpaRepository nodeRepo;
    private final FolderJpaRepository folderRepo;



    @Override
    public Optional<NodeAr> loadNode(NodeId id) {
        return nodeRepo.findById(id.toLong())
            .map(this::toNodeAr);
    }

    @Override
    public Folder createFolder(CreateFolderCmd cmd) {
        FolderJpaEntity entity = new FolderJpaEntity(
            TSID.Factory.getTsid().toLong(),
            bookRepo.getReferenceById(cmd.getBookId().toLong()),
            cmd.getTitle().getValue(),
            folderRepo.getReferenceById(cmd.getParentNodeId().toLong())
        );
        nodeRepo.persist(entity);
        return toNodeAr(entity);
    }

    @Override
    public Page createPage(CreatePageCmd cmd) {
        PageJpaEntity entity = new PageJpaEntity(
            TSID.Factory.getTsid().toLong(),
            bookRepo.getReferenceById(cmd.getBookId().toLong()),
            cmd.getTitle().getValue(),
            folderRepo.getReferenceById(cmd.getParentNodeId().toLong()),
            cmd.getContent()
        );
        nodeRepo.persist(entity);
        return toNodeAr(entity);
    }

    @Override
    public <N extends NodeAr> N saveNode(N node) {
        NodeJpaEntity entity = nodeRepo.findById(node.getId().toLong())
            .orElseThrow(() -> ApiCode3.DATA_NOT_FOUND.feedback("node를 찾을 수 없습니다."));
        nodeRepo.merge(entity);
        return node;
    }

    @Override
    public void deleteNode(NodeId id) {
        nodeRepo.deleteById(id.toLong());
    }



    private <AR extends NodeAr> AR toNodeAr(NodeJpaEntity nodeJpaEntity) {
        if(nodeJpaEntity instanceof FolderJpaEntity f){
            return (AR) new Folder(
                BookId.from(f.getBook().getId()),
                NodeId.from(f.getParentNode() == null ? null : f.getParentNode().getId()),
                NodeId.from(f.getId()),
                new Title(f.getTitle())
            );
        } else if(nodeJpaEntity instanceof PageJpaEntity p){
            return (AR) new Page(
                BookId.from(p.getBook().getId()),
                NodeId.from(p.getParentNode() == null ? null : p.getParentNode().getId()),
                NodeId.from(p.getId()),
                new Title(p.getTitle())
            );
        } else {
            assert false : "Unknown node type";
            throw new IllegalArgumentException("Unknown node type");
        }
    }

    private NodeJpaEntity toJpaEntity(NodeAr node){
        if(node instanceof Folder f){
            return new FolderJpaEntity(
                f.getId().toLong(),
                bookRepo.getReferenceById(f.getBookId().toLong()),
                f.getTitle().getValue(),
                folderRepo.getReferenceById(f.getParentId().toLong())
            );
        } else if(node instanceof Page p){
            return new PageJpaEntity(
                p.getId().toLong(),
                bookRepo.getReferenceById(p.getBookId().toLong()),
                p.getTitle().getValue(),
                folderRepo.getReferenceById(p.getParentId().toLong()),
                p.getContent()
            );
        } else {
            assert false : "Unknown node type";
            throw new IllegalArgumentException("Unknown node type");
        }
    }

}