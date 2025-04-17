package org.pageflow.book.application.service;

import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.book.persistence.toc.TocFolderRepository;
import org.pageflow.book.persistence.toc.TocNodeRepository;
import org.pageflow.book.persistence.toc.TocSectionRepository;
import org.pageflow.common.result.ResultException;
import org.pageflow.common.result.code.CommonCode;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author : sechan
 */
public class TocNodeLoader {
  private final TocNodeRepository tocNodeRepository;
  private final TocSectionRepository tocSectionRepository;
  private final TocFolderRepository tocFolderRepository;
  private final Book book;

  public TocNodeLoader(
    Book book,
    TocNodeRepository tocNodeRepository,
    TocSectionRepository tocSectionRepository,
    TocFolderRepository tocFolderRepository
  ) {
    this.book = book;
    this.tocNodeRepository = tocNodeRepository;
    this.tocSectionRepository = tocSectionRepository;
    this.tocFolderRepository = tocFolderRepository;
  }

  public TocNode loadNode(Function<TocNodeRepository, Optional<TocNode>> loader) {
    Optional<TocNode> opt = loader.apply(tocNodeRepository);
    return validate(opt);
  }

  public TocFolder loadFolder(Function<TocFolderRepository, Optional<TocFolder>> loader) {
    Optional<TocFolder> opt = loader.apply(tocFolderRepository);
    return validate(opt);
  }

  public TocSection loadSection(Function<TocSectionRepository, Optional<TocSection>> loader) {
    Optional<TocSection> opt = loader.apply(tocSectionRepository);
    return validate(opt);
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private <N extends TocNode> N validate(Optional<N> nodeOpt) {
    if(nodeOpt.isPresent()) {
      N node = nodeOpt.get();
      if(node.getBook().equals(book)) {
        return node;
      }
    }
    throw new ResultException(CommonCode.DATA_NOT_FOUND, "요청된 Node를 찾을 수 없습니다.");
  }
}
