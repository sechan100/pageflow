package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.dto.*;
import org.pageflow.book.port.in.BookQueries;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.NodePersistencePort;
import org.pageflow.book.port.out.jpa.SectionPersistencePort;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookQueriesImpl implements BookQueries {
  private final NodePersistencePort nodePersistencePort;
  private final LoadAuthorPort loadAuthorPort;
  private final BookPersistencePort bookPersistencePort;
  private final SectionPersistencePort sectionPersistencePort;



  @Override
  public BookDtoWithAuthor queryBook(UUID id) {
    Book book = bookPersistencePort.findBookWithAuthorById(id).get();
    return BookDtoWithAuthor.from(book);
  }

  @Override
  public MyBooks queryMyBooks(UID uid) {
    // books
    Author author = loadAuthorPort.loadAuthor(uid).get();
    List<Book> books = bookPersistencePort.findBooksByAuthorId(uid.getValue());

    return new MyBooks(
      AuthorDto.from(author),
      books.stream().map(BookDto::from).toList()
    );
  }

  @Override
  public FolderDto queryFolder(UUID folderId) {
    TocNode node = nodePersistencePort.findById(folderId).get();
    return FolderDto.from(node);
  }

  @Override
  public SectionDto querySection(UUID sectionId) {
    TocNode node = nodePersistencePort.findById(sectionId).get();
    return SectionDto.from(node);
  }

  @Override
  public SectionDtoWithContent querySectionWithContent(UUID sectionId) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    return SectionDtoWithContent.from(section);
  }
}
