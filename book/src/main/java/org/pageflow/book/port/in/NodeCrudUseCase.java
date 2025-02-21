package org.pageflow.book.port.in;

import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDtoWithContent;

/**
 * @author : sechan
 */
public interface NodeCrudUseCase {
  FolderDto createFolder(CreateFolderCmd cmd);

  SectionDtoWithContent createSection(CreateSectionCmd cmd);
}
