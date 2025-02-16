package org.pageflow.book.persistence;


import lombok.RequiredArgsConstructor;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.boundedcontext.book.adapter.out.persistence.jpa.*;
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
public class NodePersistenceAdapter {
  private final BookRepository bookRepo;
  private final NodeRepository nodeRepo;
  private final FolderRepository folderRepo;
  private final SectionRepository sectionRepo;


  @Override
  public Folder createFolder(BookId bookId, NodeId parentNodeId, Title title, int ov) {
    Folder parentFolder = folderRepo.getReferenceById(parentNodeId.toLong());

    Folder entity = new Folder(
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
    Folder parentFolder = folderRepo.getReferenceById(parentNodeId.toLong());

    Section entity = new Section(
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
    Folder entity = folderRepo.findById(folder.getId().toLong()).get();
    entity.setTitle(folder.getTitle().getValue());
    return folder;
  }

  @Override
  public Section saveSection(Section section) {
    Section entity = sectionRepo.findById(section.getId().toLong()).get();
    entity.setTitle(section.getTitle().getValue());
    entity.setContent(section.getContent());
    return section;
  }

  @Override
  public void deleteNode(NodeId id) {
    nodeRepo.deleteById(id.toLong());
  }


  private <N extends AbstractNode> N toDomain(TocNode nodeJpaEntity) {
    if(nodeJpaEntity instanceof Folder f){
      return (N) new Folder(
        BookId.from(f.getBook().getId()),
        NodeId.from(f.getId()),
        Title.from(f.getTitle())
      );
    } else if(nodeJpaEntity instanceof Section p){
      return (N) new Section(
        BookId.from(p.getBook().getId()),
        NodeId.from(p.getId()),
        Title.from(p.getTitle()),
        p.getContent()
      );
    } else {
      assert false:"Unknown node type";
      throw new IllegalArgumentException("Unknown node type");
    }
  }
}