package org.pageflow.book.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.persistence.toc.TocFolderRepository;
import org.pageflow.book.persistence.toc.TocNodeRepository;
import org.pageflow.book.persistence.toc.TocSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TocNodeLoaderFactory {
  private final TocNodeRepository tocNodeRepository;
  private final TocFolderRepository tocFolderRepository;
  private final TocSectionRepository tocSectionRepository;

  public TocNodeLoader createLoader(Book book) {
    return new TocNodeLoader(
      book,
      tocNodeRepository,
      tocSectionRepository,
      tocFolderRepository
    );
  }
}
