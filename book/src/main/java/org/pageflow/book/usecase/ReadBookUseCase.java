package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.book.application.dto.book.PublishedBookDto;
import org.pageflow.book.application.dto.node.SectionContentDto;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.LoadAuthorPort;
import org.pageflow.book.persistence.toc.ReadTocNodePort;
import org.pageflow.book.persistence.toc.SectionContentPersistencePort;
import org.pageflow.book.persistence.toc.TocPersistencePort;
import org.pageflow.common.result.Result;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadBookUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final SectionContentPersistencePort sectionContentPersistencePort;
  private final ReadTocNodePort readTocPort;
  private final LoadAuthorPort loadAuthorPort;
  private final TocPersistencePort tocPersistencePort;

  /**
   * @code BOOK_ACCESS_DENIED: 책을 읽을 권한이 없는 경우
   */
  public Result<PublishedBookDto> readBook(UID uid, UUID bookId) {
    Book book = bookPersistencePort.findBookWithAuthorById(bookId).get();
    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.READ);
    if(grant.isFailure()) {
      return grant;
    }
    UID authorId = book.getAuthor().getUid();
    AuthorProfileDto authorProfileDto = loadAuthorPort.loadAuthorProfile(authorId);
    Toc toc = tocPersistencePort.loadReadonlyToc(book);
    int totalCharCount = sectionContentPersistencePort.sumCharCountByBookId(bookId);

    return Result.ok(
      new PublishedBookDto(book, authorProfileDto, TocDto.from(toc), totalCharCount)
    );
  }

  public Result<SectionContentDto> readSectionContent(UID uid, UUID bookId, UUID sectionId) {
    Book book = bookPersistencePort.findById(bookId).get();
    // 권한 검사 =====
    BookAccessGranter accessGranter = new BookAccessGranter(uid, book);
    Result grant = accessGranter.grant(BookAccess.READ);
    if(grant.isFailure()) {
      return grant;
    }
    TocSection section = readTocPort.readSection(book, sectionId).get();
    return Result.ok(SectionContentDto.from(section));
  }


}
