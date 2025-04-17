package org.pageflow.book.persistence.toc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SaveTocNodePersistenceAdapter implements SaveTocFolderPort {
  private final TocFolderRepository tocFolderRepository;

  @Override
  public TocFolder save(TocFolder folder) {
    return tocFolderRepository.save(folder);
  }
}
