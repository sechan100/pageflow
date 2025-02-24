package org.pageflow.book.port.in;

import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodeCrudUseCase {
  FolderDto createFolder(BookPermission permission, CreateFolderCmd cmd);
  FolderDto queryFolder(BookPermission permission, UUID folderId);
  FolderDto updateFolder(BookPermission permission, UpdateFolderCmd cmd);
  void deleteFolder(BookPermission permission, UUID folderId);

  SectionDtoWithContent createSection(BookPermission permission, CreateSectionCmd cmd);
  SectionDto querySection(BookPermission permission, UUID sectionId);
  SectionDtoWithContent querySectionWithContent(BookPermission permission, UUID sectionId);
  SectionDtoWithContent updateSection(BookPermission permission, UpdateSectionCmd cmd);
  void deleteSection(BookPermission permission, UUID sectionId);
}
