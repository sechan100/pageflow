package org.pageflow.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.toc.FolderCreateCmd;
import org.pageflow.book.domain.toc.LastIndexInserter;
import org.pageflow.book.domain.toc.SectionCreateCmd;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.persistence.FolderRepository;
import org.pageflow.book.persistence.NodeRepository;
import org.pageflow.book.persistence.BookRepository;
import org.pageflow.book.persistence.SectionRepository;
import org.pageflow.book.shared.TocNodeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NodeCrudService {
  private final BookRepository bookRepository;
  private final FolderRepository folderRepository;
  private final SectionRepository sectionRepository;
  private final NodeRepository nodeRepository;


  public TocDto.Node createFolder(FolderCreateCmd cmd) {
    UUID bookId = cmd.getBookId();
    UUID parentId = cmd.getParentNodeId();
    UUID folderId = UUID.randomUUID();

    // Folder 생성
    Book bookProxy = bookRepository.getReferenceById(cmd.getBookId());
    Folder folder = Folder.builder()
      .id(folderId)
      .book(bookProxy)
      .title(cmd.getTitle().getValue())
      .parentNode(null)
      .ov(0)
      .build();
    folderRepository.persist(folder);

    LastIndexInserter inserter = new LastIndexInserter(bookId, parentId, nodeRepository);
    inserter.insertLast(folder);

    return new TocDto.Node(
      folder.getId(),
      folder.getTitle(),
      TocNodeType.FOLDER
    );
  }

  public TocDto.Node createSection(SectionCreateCmd cmd) {
    UUID bookId = cmd.getBookId();
    UUID parentId = cmd.getParentNodeId();
    UUID sectionId = UUID.randomUUID();

    // Section 생성
    Book bookProxy = bookRepository.getReferenceById(cmd.getBookId());
    Folder parentFolder = folderRepository.findById(parentId).orElseThrow();
    Section section = Section.builder()
      .id(sectionId)
      .book(bookProxy)
      .title(cmd.getTitle().getValue())
      .parentNode(parentFolder)
      .content(cmd.getContent())
      .ov(0)
      .build();
    sectionRepository.persist(section);

    LastIndexInserter inserter = new LastIndexInserter(bookId, parentId, nodeRepository);
    inserter.insertLast(section);

    return new TocDto.Node(
      section.getId(),
      section.getTitle(),
      TocNodeType.SECTION
    );
  }
}
