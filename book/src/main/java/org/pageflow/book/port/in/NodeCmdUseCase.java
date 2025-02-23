package org.pageflow.book.port.in;

import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDtoWithContent;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodeCmdUseCase {
  FolderDto createFolder(CreateFolderCmd cmd);
  FolderDto updateFolder(UpdateFolderCmd cmd);
  void deleteFolder(UUID folderId);

  SectionDtoWithContent createSection(CreateSectionCmd cmd);
  SectionDtoWithContent updateSection(UpdateSectionCmd cmd);
  void deleteSection(UUID sectionId);
}
