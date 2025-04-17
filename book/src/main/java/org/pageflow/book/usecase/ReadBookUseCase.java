package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.book.application.dto.book.PublishedBookDto;
import org.pageflow.book.application.dto.node.SectionContentDto;
import org.pageflow.book.application.dto.node.TocDto;
import org.pageflow.book.application.service.GrantedBookLoader;
import org.pageflow.book.application.service.TocNodeLoader;
import org.pageflow.book.application.service.TocNodeLoaderFactory;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.persistence.AuthorRepository;
import org.pageflow.book.persistence.toc.SectionContentRepository;
import org.pageflow.book.persistence.toc.TocRepository;
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
  private final GrantedBookLoader grantedBookLoader;
  private final TocNodeLoaderFactory tocNodeLoaderFactory;
  private final SectionContentRepository sectionContentRepository;
  private final AuthorRepository authorRepository;
  private final TocRepository tocRepository;

  /**
   * @code BOOK_ACCESS_DENIED: 책을 읽을 권한이 없는 경우
   */
  public PublishedBookDto readBook(UID uid, UUID bookId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.READ);
    UID authorId = book.getAuthor().getUid();
    AuthorProfileDto authorProfileDto = authorRepository.loadAuthorProfile(authorId);
    Toc toc = tocRepository.loadReadonlyToc(book);
    int totalCharCount = sectionContentRepository.sumCharCountByBookId(bookId);

    return new PublishedBookDto(book, authorProfileDto, TocDto.from(toc), totalCharCount);
  }

  public SectionContentDto readSectionContent(UID uid, UUID bookId, UUID sectionId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.READ);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocSection section = nodeLoader.loadSection(repo -> repo.findWithContentById(sectionId));
    return SectionContentDto.from(section.getSectionDetails().getContent());
  }


}
