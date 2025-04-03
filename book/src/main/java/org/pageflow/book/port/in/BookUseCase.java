package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.dto.AuthorDto;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.BookDtoWithAuthor;
import org.pageflow.book.application.dto.MyBooks;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.FolderPersistencePort;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.common.result.Result;
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
public class BookUseCase {
  private final LoadAuthorPort loadAuthorPort;
  private final ApplicationProperties applicationProperties;
  private final ImageUrlValidator imageUrlValidator;
  private final BookPersistencePort bookPersistencePort;
  private final FolderPersistencePort folderPersistencePort;
  private final FileService fileService;


  /**
   * @code FILED_VALIDATION_ERROR: coverImage file의 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: CoverImage 업로드에 실패한 경우
   */
  public Result<BookDto> createBook(
    UID authorId,
    BookTitle title,
    @Nullable MultipartFile coverImage
  ) {
    // author
    Author author = loadAuthorPort.loadAuthorProxy(authorId);
    // book
    UUID bookId = UUID.randomUUID();
    String coverImageUrl = applicationProperties.book.defaultCoverImageUrl;
    if(coverImage != null) {
      Result<FilePath> uploadResult = _uploadCoverImage(bookId, coverImage);
      if(uploadResult.isSuccess()) {
        coverImageUrl = uploadResult.getSuccessData().getWebUrl();
      } else {
        return (Result) uploadResult;
      }
    }

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

    return Result.success(BookDto.from(book));
  }

  /**
   * @code BOOK_PERMISSION_DENIED: 책 읽기 권한이 없는 경우
   */
  public Result<BookDtoWithAuthor> readBook(UID uid, BookId bookId) {
    Book book = bookPersistencePort.findBookWithAuthorById(bookId.getValue()).get();
    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.READ);
    if(grant.isFailure()) {
      return grant;
    }
    return Result.success(BookDtoWithAuthor.from(book));
  }

  public MyBooks queryMyBooks(UID uid) {
    // books
    Author author = loadAuthorPort.loadAuthor(uid).get();
    List<Book> books = bookPersistencePort.findBooksByAuthorId(uid.getValue());

    return new MyBooks(
      AuthorDto.from(author),
      books.stream().map(BookDto::from).toList()
    );
  }

  /**
   * @code BOOK_PERMISSION_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 이미 발행된 책인 경우
   */
  public Result<BookDto> changeBookTitle(UID uid, BookId bookId, BookTitle title) {
    Book book = bookPersistencePort.findById(bookId.getValue()).get();

    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // 책 제목 변경
    book.changeTitle(title);
    return Result.success(BookDto.from(book));
  }

  /**
   * @code FILED_VALIDATION_ERROR: coverImage file의 데이터가 올바르지 않은 경우
   * @code FAIL_TO_DELETE_FILE: 기존 CoverImage 삭제에 실패한 경우
   * @code FAIL_TO_UPLOAD_FILE: 새 CoverImage 업로드에 실패한 경우
   * @code BOOK_PERMISSION_DENIED: 책 권한이 없는 경우
   * @code BOOK_INVALID_STATUS: 이미 발행된 책인 경우
   */
  public Result<BookDto> changeBookCoverImage(UID uid, BookId bookId, MultipartFile coverImage) {
    Book book = bookPersistencePort.findById(bookId.getValue()).get();

    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // 내부에 저장된 이미지인 경우, 기존 이미지를 삭제 =============
    String oldUrl = book.getCoverImageUrl();
    if(imageUrlValidator.isInternalUrl(oldUrl)) {
      FilePath path = FilePath.fromWebUrl(oldUrl);
      Result deleteResult = fileService.delete(path);
      if(deleteResult.isFailure()) {
        return (Result) deleteResult;
      }
    }

    // 새 이미지 업로드 ================
    Result<FilePath> uploadResult = _uploadCoverImage(bookId.getValue(), coverImage);
    if(uploadResult.isFailure()) {
      return (Result) uploadResult;
    }
    book.changeCoverImageUrl(uploadResult.getSuccessData().getWebUrl());
    return Result.success(BookDto.from(book));
  }


  /**
   * @code BOOK_PERMISSION_DENIED: 책 권한이 없는 경우
   */
  public Result deleteBook(UID uid, BookId bookId) {
    Book book = bookPersistencePort.findById(bookId.getValue()).get();

    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.UPDATE);
    if(grant.isFailure()) {
      return grant;
    }

    bookPersistencePort.delete(book);
    return Result.success();
  }


  /**
   * @code FILED_VALIDATION_ERROR: coverImage file의 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: CoverImage 업로드에 실패한 경우
   */
  private Result<FilePath> _uploadCoverImage(UUID bookId, MultipartFile coverImage) {
    Result<FileUploadCmd> cmdResult = FileUploadCmd.createCmd(
      coverImage,
      bookId.toString(),
      FileType.BOOK_COVER_IMAGE
    );
    if(cmdResult.isFailure()) {
      return (Result) cmdResult;
    }
    return fileService.upload(cmdResult.getSuccessData());
  }
}
