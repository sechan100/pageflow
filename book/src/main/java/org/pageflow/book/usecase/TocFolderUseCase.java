package org.pageflow.book.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.service.GrantedBookLoader;
import org.pageflow.book.application.service.TocNodeLoader;
import org.pageflow.book.application.service.TocNodeLoaderFactory;
import org.pageflow.book.domain.book.constants.BookAccess;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.EditableFolder;
import org.pageflow.book.domain.toc.entity.FolderDesign;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
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
  private final GrantedBookLoader grantedBookLoader;
  private final TocNodeLoaderFactory tocNodeLoaderFactory;

  public FolderDto changeFolderDesign(NodeIdentifier identifier, FolderDesign newDesign) {
    Book book = grantedBookLoader.loadBookWithGrant(identifier.getUid(), identifier.getBookId(), BookAccess.WRITE);

    TocNodeLoader nodeLoader = tocNodeLoaderFactory.createLoader(book);
    TocFolder folder = nodeLoader.loadFolder(repo -> repo.findById(identifier.getNodeId()));
    EditableFolder editableFolder = new EditableFolder(folder);

    editableFolder.changeDesign(newDesign);
    return FolderDto.from(folder);
  }

}
