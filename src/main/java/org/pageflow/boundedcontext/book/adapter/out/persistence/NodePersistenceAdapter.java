package org.pageflow.boundedcontext.book.adapter.out.persistence;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.port.in.CreateFolderCmd;
import org.pageflow.boundedcontext.book.port.in.CreatePageCmd;
import org.pageflow.boundedcontext.book.port.out.NodePersistencePort;
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
    private final PageJpaRepository pageRepo;



    @Override
    public Folder createFolder(CreateFolderCmd cmd) {
        FolderJpaEntity parentFolder;
        if(cmd.getParentNodeId().equals(NodeId.from(0L))){
            parentFolder = null;
        } else {
            parentFolder = folderRepo.getReferenceById(cmd.getParentNodeId().toLong());
        }

        FolderJpaEntity entity = new FolderJpaEntity(
            TSID.Factory.getTsid().toLong(), // id
            bookRepo.getReferenceById(cmd.getBookId().toLong()), // book
            cmd.getTitle().getValue(), // title
            parentFolder // parent
        );
        nodeRepo.persist(entity);
        return toDomain(entity);
    }

    @Override
    public Page createPage(CreatePageCmd cmd) {
        FolderJpaEntity parentFolder;
        if(cmd.getParentNodeId().equals(NodeId.from(0L))){
            parentFolder = null;
        } else {
            parentFolder = folderRepo.getReferenceById(cmd.getParentNodeId().toLong());
        }

        PageJpaEntity entity = new PageJpaEntity(
            TSID.Factory.getTsid().toLong(), // id
            bookRepo.getReferenceById(cmd.getBookId().toLong()), // book
            cmd.getTitle().getValue(), // title
            parentFolder , // parent
            cmd.getContent() // content
        );
        nodeRepo.persist(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Folder> loadFolder(NodeId id) {
        return nodeRepo.findById(id.toLong())
            .map(this::toDomain);
    }

    @Override
    public Optional<Page> loadPage(NodeId id) {
        return nodeRepo.findById(id.toLong())
            .map(this::toDomain);
    }

    @Override
    public Folder saveFolder(Folder folder) {
        FolderJpaEntity entity = folderRepo.findById(folder.getId().toLong()).get();
        entity.setTitle(folder.getTitle().getValue());
        return folder;
    }

    @Override
    public Page savePage(Page page) {
        PageJpaEntity entity = pageRepo.findById(page.getId().toLong()).get();
        entity.setTitle(page.getTitle().getValue());
        entity.setContent(page.getContent());
        return page;
    }

    @Override
    public void deleteNode(NodeId id) {
        nodeRepo.deleteById(id.toLong());
    }



    private <N extends AbstractNode> N toDomain(NodeJpaEntity nodeJpaEntity) {
        if(nodeJpaEntity instanceof FolderJpaEntity f){
            return (N) new Folder(
                BookId.from(f.getBook().getId()),
                NodeId.from(f.getId()),
                Title.from(f.getTitle())
            );
        } else if(nodeJpaEntity instanceof PageJpaEntity p){
            return (N) new Page(
                BookId.from(p.getBook().getId()),
                NodeId.from(p.getId()),
                Title.from(p.getTitle()),
                p.getContent()
            );
        } else {
            assert false : "Unknown node type";
            throw new IllegalArgumentException("Unknown node type");
        }
    }
}