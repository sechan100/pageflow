package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.domain.book.BookAccessGranter;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.toc.entity.FolderDesign;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.LoadEditableTocNodePort;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.common.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TocFolderUseCase {
  private final BookPersistencePort bookPersistencePort;
  private final LoadEditableTocNodePort loadNodePort;

  public Result<FolderDto> changeFolderDesign(NodeIdentifier identifier, FolderDesign newDesign) {
    return Result.fromOptional(bookPersistencePort.findById(identifier.getBookId()))
      .flatMap(book -> new BookAccessGranter(identifier.getUid(), book)
        .grant(BookAccess.WRITE)
        .map(book)
      )
      .flatMap(book -> loadNodePort.loadEditableFolder(book, identifier.getNodeId()))
      .flatMap(folder -> folder
        .changeDesign(newDesign)
        .map(folder)
      )
      .map(FolderDto::from);
  }

}
