package org.pageflow.book.port.in;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.book.application.dto.book.PublishedBookDto;
import org.pageflow.book.application.dto.node.SectionContentDto;
import org.pageflow.book.domain.BookAccessGranter;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.enums.BookAccess;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.ReadTocPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.book.port.out.jpa.NodeContentPersistencePort;
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
  private final NodeContentPersistencePort nodeContentPersistencePort;
  private final ReadTocPort readTocPort;
  private final LoadAuthorPort loadAuthorPort;

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
    Toc toc = readTocPort.loadReadonlyToc(book);
    int totalCharCount = nodeContentPersistencePort.sumCharCountByBookId(bookId);

    return Result.success(
      new PublishedBookDto(book, authorProfileDto, toc.buildTreeDto(), totalCharCount)
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
    TocNode section = readTocPort.readSection(book, sectionId).get();
    return Result.success(SectionContentDto.from(section));
  }


}
