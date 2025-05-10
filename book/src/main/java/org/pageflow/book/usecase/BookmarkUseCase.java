package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.book.BookmarkDto;
import org.pageflow.book.application.service.GrantedBookLoader;
import org.pageflow.book.application.service.TocNodeLoader;
import org.pageflow.book.application.service.TocNodeLoaderFactory;
import org.pageflow.book.domain.book.Author;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.book.entity.Bookmark;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.persistence.AuthorRepository;
import org.pageflow.book.persistence.BookmarkRepository;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkUseCase {
  private final GrantedBookLoader grantedBookLoader;
  private final TocNodeLoaderFactory tocNodeLoaderFactory;
  private final BookmarkRepository bookmarkRepository;
  private final AuthorRepository authorRepository;

  public BookmarkDto markReadingPoint(UID uid, UUID bookId, UUID tocNodeId, int sectionContentElementId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.READ);
    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    Optional<Bookmark> prevBookmarkOpt = bookmarkRepository.findByUserIdAndBookId(uid.getValue(), bookId);
    TocNode tocNode = nodeLoader.loadNode(repo -> repo.findById(tocNodeId));
    if(prevBookmarkOpt.isPresent()) {
      Bookmark prevBookmark = prevBookmarkOpt.get();
      prevBookmark.update(tocNode, sectionContentElementId);
      return new BookmarkDto(prevBookmark);
    } else {
      Author author = authorRepository.loadAuthorProxy(uid);
      Bookmark bookmark = Bookmark.create(author, book, tocNode, sectionContentElementId);
      bookmark = bookmarkRepository.save(bookmark);
      return new BookmarkDto(bookmark);
    }
  }

  public BookmarkDto getBookmarkOrNull(UID uid, UUID bookId) {
    Book book = grantedBookLoader.loadBookWithGrant(uid, bookId, BookAccess.READ);
    Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByUserIdAndBookId(uid.getValue(), bookId);
    if(bookmarkOpt.isPresent()) {
      return new BookmarkDto(bookmarkOpt.get());
    } else {
      return null;
    }
  }
}
