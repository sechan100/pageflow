package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.dto.AuthorDto;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.dto.BookDtoWithAuthor;
import org.pageflow.book.dto.MyBooks;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.FolderPersistencePort;
import org.pageflow.common.permission.PermissionRequired;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.common.user.UID;
import org.pageflow.common.validation.ImageUrlValidator;
import org.pageflow.file.model.FilePath;
import org.pageflow.file.model.FileUploadCmd;
import org.pageflow.file.service.FileService;
import org.pageflow.file.shared.FileType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookService implements BookUseCase {
  private final LoadAuthorPort loadAuthorPort;
  private final ApplicationProperties applicationProperties;
  private final ImageUrlValidator imageUrlValidator;
  private final BookPersistencePort bookPersistencePort;
  private final FolderPersistencePort folderPersistencePort;
  private final FileService fileService;


  @Override
  public BookDto createBook(
    UID authorId,
    BookTitle title,
    @Nullable MultipartFile coverImage
  ) {
    // author
    Author author = loadAuthorPort.loadAuthorProxy(authorId);

    // book
    UUID bookId = UUID.randomUUID();
    String coverImageUrl = coverImage != null
      ?
    this.uploadCoverImage(bookId, coverImage)
      :
    applicationProperties.book.defaultCoverImageUrl;

    Book book = Book.create(
      bookId,
      author,
      title,
      coverImageUrl
    );
    bookPersistencePort.persist(book);

    // root folder
    Folder rootFolder = Folder.createRootFolder(book);
    folderPersistencePort.persist(rootFolder);

    return BookDto.from(book);
  }


  @Override
  @PermissionRequired(
    actions = { "READ" },
    permissionType = BookPermission.class
  )
  public BookDtoWithAuthor queryBook(@BookId UUID bookId) {
    Book book = bookPersistencePort.findBookWithAuthorById(bookId).get();
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
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public BookDto changeBookTitle(@BookId UUID bookId, BookTitle title) {
    Book book = bookPersistencePort.findById(bookId).get();
    book.changeTitle(title);
    return BookDto.from(book);
  }

  @Override
  @PermissionRequired(
    actions = { "EDIT" },
    permissionType = BookPermission.class
  )
  public BookDto changeBookCoverImage(@BookId UUID bookId, MultipartFile coverImage) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 내부에 저장된 이미지인 경우, 기존 이미지를 삭제
    String oldUrl = book.getCoverImageUrl();
    if(imageUrlValidator.isInternalUrl(oldUrl)){
      fileService.delete(oldUrl);
    }

    String newCoverImageUrl = this.uploadCoverImage(bookId, coverImage);
    book.changeCoverImageUrl(newCoverImageUrl);
    return BookDto.from(book);
  }

  @Override
  @PermissionRequired(
    actions = { "DELETE" },
    permissionType = BookPermission.class
  )
  public void deleteBook(@BookId UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();
    bookPersistencePort.delete(book);
  }


  private String uploadCoverImage(UUID bookId, MultipartFile coverImage) {
    // 새 이미지 업로드
    FileUploadCmd cmd = new FileUploadCmd(
      bookId.toString(),
      FileType.BOOK.COVER_IMAGE,
      coverImage
    );
    FilePath path = fileService.upload(cmd);
    return path.getWebUrl();
  }
}
