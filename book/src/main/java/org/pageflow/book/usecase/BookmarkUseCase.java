package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.TocNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final TocNodeRepository tocNodeRepository;

}
