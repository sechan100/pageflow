package org.pageflow.test.module.book.utils;

import lombok.Getter;
import org.pageflow.book.application.dto.FolderDto;
import org.pageflow.book.application.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.EditTocUseCase;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
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
    return editTocUseCase.createFolder(cmd).getSuccessData();
  }

  public SectionDtoWithContent create(CreateSectionCmd cmd) {
    return editTocUseCase.createSection(cmd).getSuccessData();
  }
}
