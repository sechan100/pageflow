package org.pageflow.book.service;

import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.dto.AuthorDto;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.port.out.persistence.BookRepository;
import org.pageflow.book.port.out.persistence.FolderRepository;
import org.pageflow.book.port.out.persistence.NodeRepository;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaEntity;
import org.pageflow.boundedcontext.user.adapter.out.persistence.jpa.ProfileJpaRepository;
import org.pageflow.shared.type.TSID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
  private final ProfileJpaRepository profileJpaRepository;
  private final BookRepository bookRepository;
  private final NodeRepository nodeRepository;
  private final FolderRepository folderRepository;


  public BookDto.Basic createBook(
    UID authorId,
    Title title,
    CoverImageUrl coverImageUrl
  ) {
    // author
    ProfileJpaEntity profile = profileJpaRepository.getReferenceById(cmd.getAuthorId().toLong());

    // book
    Long bookId = TSID.Factory.getTsid().toLong();
    Book book = Book.builder()
      .id(bookId)
      .author(profile)
      .title(title.getValue())
      .coverImageUrl(coverImageUrl.getValue())
      .build();
    bookRepository.persist(book);

    // root folder
    Folder rootFolder = new Folder(
      TSID.Factory.getTsid().toLong(),
      book,
      ":root",
      null,
      0
    );
    folderRepository.persist(rootFolder);

    return toDto(book);
  }

  public BookDto.Basic changeBookTitle(BookId id, Title title) {
    Book book = persistPort.loadBook(id).get();
    book.changeTitle(title);
    persistPort.saveBook(book);
    return toDto(book);
  }

  public BookDto.Basic changeBookCoverImage(BookId id, CoverImageUrl url) {
    Book book = persistPort.loadBook(id).get();
    book.changeCoverImageUrl(url);
    persistPort.saveBook(book);
    return toDto(book);
  }

  public BookDto.Basic queryBook(BookId id) {
    Book entity = bookRepository.findById(id.toLong()).get();
    return new BookDto.Basic(
      new TSID(entity.getId()),
      entity.getTitle(),
      entity.getCoverImageUrl()
    );
  }

  public Tuple2<AuthorDto, List<BookDto.Basic>> queryBooksByAuthorId(UID uid) {
    // books
    List<Book> entities = bookRepository.findWithAuthorByAuthorId(uid.toLong());
    List<BookDto.Basic> books = entities.stream()
      .map(entity -> new BookDto.Basic(
        new TSID(entity.getId()),
        entity.getTitle(),
        entity.getCoverImageUrl()
      ))
      .toList();

    // author
    ProfileJpaEntity authorEntity = entities.get(0).getAuthor();
    AuthorDto author = new AuthorDto(
      new TSID(authorEntity.getId()),
      authorEntity.getPenname(),
      authorEntity.getProfileImageUrl()
    );

    return new Tuple2<>(author, books);
  }

  public FolderDto.Basic queryFolder(NodeId folderId) {
    TocNode entity = nodeRepository.findById(folderId.toLong()).get();
    return new FolderDto.Basic(
      new TSID(entity.getId()),
      entity.getTitle()
    );
  }

  public SectionDto.MetaData querySectionMetadata(NodeId sectionId) {
    Section entity = (Section) nodeRepository.findById(sectionId.toLong()).get();
    return new SectionDto.MetaData(
      new TSID(entity.getId()),
      entity.getTitle()
    );
  }

  public SectionDto.WithContent querySectionWithContent(NodeId sectionId) {
    Section entity = (Section) nodeRepository.findById(sectionId.toLong()).get();
    return new SectionDto.WithContent(
      new TSID(entity.getId()),
      entity.getTitle(),
      entity.getContent()
    );
  }


  private BookDto.Basic toDto(Book book) {
    return new BookDto.Basic(
      book.getId().getValue(),
      book.getTitle().getValue(),
      book.getCoverImageUrl().getValue()
    );
  }
}
