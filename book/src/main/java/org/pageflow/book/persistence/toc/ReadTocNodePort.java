package org.pageflow.book.persistence.toc;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReadTocNodePort {
  private final TocSectionPersistencePort tocSectionPersistencePort;

  public Optional<TocSection> readSection(Book book, UUID sectionId) {
    Optional<TocSection> section = tocSectionPersistencePort.findWithContentByIdAndIsEditable(sectionId, false);
    section.ifPresent(s -> {
      Preconditions.checkState(s.getBook().equals(book));
    });

    return section;
  }
}
