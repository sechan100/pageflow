package org.pageflow.test.module.book.persistence;

import lombok.RequiredArgsConstructor;
import org.pageflow.book.persistence.toc.TocRepository;
import org.pageflow.test.shared.PageflowTest;

/**
 * @author : sechan
 */
@PageflowTest
@RequiredArgsConstructor
public class TocRepositoryTest {
  private final TocRepository tocRepository;
}
