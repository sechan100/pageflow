package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.application.dto.book.MyBooksDto;
import org.pageflow.book.application.dto.book.WithAuthorBookDto;
import org.pageflow.book.domain.book.Author;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.BookDescriptionHtmlContent;
import org.pageflow.book.domain.book.BookTitle;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.persistence.AuthorRepository;
import org.pageflow.book.persistence.BookRepository;
import org.pageflow.book.persistence.toc.TocFolderRepository;
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
import java.util.Optional;
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
  private final AuthorRepository authorRepository;
  private final ImageUrlValidator imageUrlValidator;
  private final FileService fileService;

  private final BookRepository bookRepository;
  private final TocFolderRepository tocFolderRepository;


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
    Author author = authorRepository.loadAuthorProxy(authorId);
    // book
    BookTitle bookTitle = BookTitle.create(title);
    Book book = new Book(author, bookTitle);
    if(coverImage != null) {
      FilePath coverImagePath = uploadCoverImage(book.getId(), coverImage);
      String coverImageUrl = coverImagePath.getWebUrl();
      book.changeCoverImageUrl(coverImageUrl);
    }
    Book savedBook = bookRepository.save(book);
    // root folder
    TocFolder rootFolder = TocFolder.createRootFolder(savedBook);
    tocFolderRepository.save(rootFolder);

    return Result.ok(new BookDto(book));
  }

  /**
   * {@link BookAccess#AUTHOR} 권한 필요
   *
   * @code BOOK_ACCESS_DENIED: 책 읽기 권한이 없는 경우
   */
  public Result<WithAuthorBookDto> getBook(UID uid, UUID bookId) {
    Book book = bookRepository.findBookWithAuthorById(bookId).get();
    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.AUTHOR);
    if(grant.isFailure()) {
      return grant;
    }
    return Result.ok(WithAuthorBookDto.from(book));
  }

  public MyBooksDto queryMyBooks(UID uid) {
    // books
    List<Book> books = bookRepository.findBooksByAuthorId(uid.getValue());

    return new MyBooksDto(
      books.stream().map(BookDto::new).toList()
    );
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   */
  public Result<BookDto> changeBookTitle(UID uid, UUID bookId, String title) {
    Book book = bookRepository.findById(bookId).get();

    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.WRITE);
    if(grant.isFailure()) {
      return grant;
    }

    // 책 제목 변경
    BookTitle bookTitle = BookTitle.create(title);
    book.changeTitle(bookTitle);
    return Result.ok(new BookDto(book));
  }

  /**
   * @return {@link org.pageflow.book.application.BookCode}, {@link org.pageflow.file.shared.FileCode}
   */
  public Result<BookDto> changeBookCoverImage(UID uid, UUID bookId, MultipartFile coverImage) {
    Book book = bookRepository.findById(bookId).get();

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
      fileService.delete(path);
    }

    // 새 이미지 업로드 ================
    FilePath coverImagePath = uploadCoverImage(bookId, coverImage);
    book.changeCoverImageUrl(coverImagePath.getWebUrl());
    return Result.ok(new BookDto(book));
  }

  public Result<BookDto> changeBookDescription(UID uid, UUID bookId, String description) {
    Book book = bookRepository.findById(bookId).get();

    // 권한 검사 =========================
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.AUTHOR);
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
    return Result.ok(new BookDto(book));
  }

  /**
   * @code BOOK_ACCESS_DENIED: 책 권한이 없는 경우
   */
  public Result<Void> deleteBook(UID uid, UUID bookId) {
    Book book = bookRepository.findById(bookId).get();

    // 권한 검사 ===========
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result<Void> grant = accessGranter.grant(BookAccess.AUTHOR);
    if(grant.isFailure()) {
      return grant;
    }

    // 책 삭제 ===========================
    Optional<TocFolder> editableFolder = tocFolderRepository.findRootFolder(bookId, true, TocNodeConfig.ROOT_FOLDER_TITLE);
    editableFolder.ifPresent(tocFolderRepository::delete);
    Optional<TocFolder> readOnlyFolder = tocFolderRepository.findRootFolder(bookId, false, TocNodeConfig.ROOT_FOLDER_TITLE);
    readOnlyFolder.ifPresent(tocFolderRepository::delete);
    bookRepository.delete(book);
    return Result.ok();
  }


  /**
   * @code FILED_VALIDATION_ERROR: coverImage file의 데이터가 올바르지 않은 경우
   * @code FAIL_TO_UPLOAD_FILE: CoverImage 업로드에 실패한 경우
   */
  private FilePath uploadCoverImage(UUID bookId, MultipartFile coverImage) {
    FileUploadCmd cmd = FileUploadCmd.createCmd(
      coverImage,
      bookId.toString(),
      FileType.BOOK_COVER_IMAGE
    );
    return fileService.upload(cmd);
  }
}
