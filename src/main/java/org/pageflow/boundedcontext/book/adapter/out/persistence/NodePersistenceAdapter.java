package org.pageflow.boundedcontext.book.adapter.out.persistence;


import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
import org.pageflow.boundedcontext.book.domain.*;
import org.pageflow.boundedcontext.book.port.out.NodePersistencePort;
import org.pageflow.shared.type.TSID;
import org.springframework.lang.Nullable;
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
    private final SectionJpaRepository sectionRepo;



    @Override
    public Folder createFolder(BookId bookId, NodeId parentNodeId, Title title, int ov) {
        FolderJpaEntity parentFolder = getParentNodeOrNull(parentNodeId);

        FolderJpaEntity entity = new FolderJpaEntity(
            TSID.Factory.getTsid().toLong(), // id
            bookRepo.getReferenceById(bookId.toLong()), // book
            title.getValue(), // title
            parentFolder, // parent
            ov // ov
        );
        nodeRepo.persist(entity);
        return toDomain(entity);
    }

    @Override
    public Section createSection(BookId bookId, NodeId parentNodeId, Title title, String content, int ov) {
        FolderJpaEntity parentFolder = getParentNodeOrNull(parentNodeId);

        SectionJpaEntity entity = new SectionJpaEntity(
            TSID.Factory.getTsid().toLong(), // id
            bookRepo.getReferenceById(bookId.toLong()), // book
            title.getValue(), // title
            parentFolder, // parent
            content, // content
            ov // ov
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
    public Optional<Section> loadSection(NodeId id) {
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
    public Section saveSection(Section section) {
        SectionJpaEntity entity = sectionRepo.findById(section.getId().toLong()).get();
        entity.setTitle(section.getTitle().getValue());
        entity.setContent(section.getContent());
        return section;
    }

    @Override
    public void deleteNode(NodeId id) {
        nodeRepo.deleteById(id.toLong());
    }

    /**
     * parentNode들의 자식중에서 가장 큰 ov 값을 찾는다.
     */
    @Override
    public Optional<Integer> loadMaxOvAmongSiblings(BookId bookId, NodeId parentNodeId) {
        return nodeRepo.findMaxOvAmongSiblings(bookId.toLong(), parentNodeId.toLong());
    }

    /**
     *
     */
    @Nullable
    private FolderJpaEntity getParentNodeOrNull(NodeId parentNodeId) {
        Long idOrNull = DomainValueConveter.convertNodeId(parentNodeId);
        if(parentNodeId.equals(NodeId.ROOT)){
            return null;
        } else {
            return folderRepo.getReferenceById(parentNodeId.toLong());
        }
    }


    private <N extends AbstractNode> N toDomain(NodeJpaEntity nodeJpaEntity) {
        if(nodeJpaEntity instanceof FolderJpaEntity f){
            return (N) new Folder(
                BookId.from(f.getBook().getId()),
                NodeId.from(f.getId()),
                Title.from(f.getTitle())
            );
        } else if(nodeJpaEntity instanceof SectionJpaEntity p){
            return (N) new Section(
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