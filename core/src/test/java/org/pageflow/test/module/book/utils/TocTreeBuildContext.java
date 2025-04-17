package org.pageflow.test.module.book.utils;

import lombok.Getter;
import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.cmd.CreateFolderCmd;
import org.pageflow.book.usecase.cmd.CreateSectionCmd;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
@Getter
public class TocTreeBuildContext {
  private final UID uid;
  private final UUID bookId;
  private final EditTocUseCase editTocUseCase;

  public TocTreeBuildContext(UID uid, UUID bookId, EditTocUseCase editTocUseCase) {
    this.uid = uid;
    this.bookId = bookId;
    this.editTocUseCase = editTocUseCase;
  }

  public FolderDto create(CreateFolderCmd cmd) {
    return editTocUseCase.createFolder(cmd);
  }

  public WithContentSectionDto create(CreateSectionCmd cmd) {
    return editTocUseCase.createSection(cmd);
  }
}
