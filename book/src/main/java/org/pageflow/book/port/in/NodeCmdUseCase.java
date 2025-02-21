package org.pageflow.book.port.in;

import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDtoWithContent;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodeCmdUseCase {
  FolderDto createFolder(FolderCreateCmd cmd);
  FolderDto updateFolder(FolderUpdateCmd cmd);
  void deleteFolder(UUID folderId);

  SectionDtoWithContent createSection(SectionCreateCmd cmd);
  SectionDtoWithContent updateSection(SectionUpdateCmd cmd);
  void deleteSection(UUID sectionId);
}
