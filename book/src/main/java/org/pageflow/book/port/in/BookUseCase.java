package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.AuthorDto;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.BookDtoWithAuthor;
import org.pageflow.book.application.dto.MyBooks;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.BookDescriptionHtmlContent;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.Folder;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.NodePersistencePort;
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
  private final ApplicationProperties applicationProperties;
  private final LoadAuthorPort loadAuthorPort;
  private final BookPersistencePort bookPersistencePort;
  private final NodePersistencePort nodePersistencePort;
  private final ImageUrlValidator imageUrlValidator;
  private final FileService fileService;


  /**
   * @code FILED_VALIDATION_ERROR: coverImage file의 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: CoverImage 업로드에 실패한 경우
   */
  public Result<BookDto> createBook(
    UID authorId,
    String title,
    @Nullable MultipartFile coverImage
  ) {
    // author
    Author author = loadAuthorPort.loadAuthorProxy(authorId);
    // book
    UUID bookId = UUID.randomUUID();
    BookTitle bookTitle = BookTitle.create(title);
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
      bookTitle,
      coverImageUrl
    );
    bookPersistencePort.persist(book);

    // root folder
    Folder rootFolder = Folder.createRootFolder(book);
    nodePersistencePort.persist(rootFolder);

    return Result.success(BookDto.from(book));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책 읽기 권한이 없는 경우
   */
  public Result<BookDtoWithAuthor> readBook(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findBookWithAuthorById(bookId).get();
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
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   */
  public Result<BookDto> changeBookTitle(UID uid, UUID bookId, String title) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // 책 제목 변경
    BookTitle bookTitle = BookTitle.create(title);
    book.changeTitle(bookTitle);
    return Result.success(BookDto.from(book));
  }

  /**
   * @return {@link org.pageflow.book.application.BookCode}, {@link org.pageflow.file.shared.FileCode}
   */
  public Result<BookDto> changeBookCoverImage(UID uid, UUID bookId, MultipartFile coverImage) {
    Book book = bookPersistencePort.findById(bookId).get();

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
    Result<FilePath> uploadResult = _uploadCoverImage(bookId, coverImage);
    if(uploadResult.isFailure()) {
      return (Result) uploadResult;
    }
    book.changeCoverImageUrl(uploadResult.getSuccessData().getWebUrl());
    return Result.success(BookDto.from(book));
  }

  public Result<BookDto> changeBookDescription(UID uid, UUID bookId, String description) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 권한 검사 =========================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.UPDATE);
    if(grant.isFailure()) {
      return grant;
    }

    // 책 설명 변경 =====================
    BookDescriptionHtmlContent bookDescription = new BookDescriptionHtmlContent(description);
    if(!bookDescription.getIsSanitizationConsistent()) {
      log.warn("""
        Book({})의 description의 html sanitize 결과가 원본과 다릅니다.
        [original]{}
        =================================================================
        [sanitized]{}
        """, bookId, description, bookDescription.getContent());
    }
    book.changeDescription(bookDescription);
    return Result.success(BookDto.from(book));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   */
  public Result deleteBook(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findById(bookId).get();

    // 권한 검사 ===========
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.UPDATE);
    if(grant.isFailure()) {
      return grant;
    }

    // 책 삭제 ===========================
    List<TocNode> nodes = nodePersistencePort.findAllByBookId(bookId);
    for(TocNode node : nodes) {
      nodePersistencePort.delete(node);
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
