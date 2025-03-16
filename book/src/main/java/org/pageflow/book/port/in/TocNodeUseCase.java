package org.pageflow.book.port.in;

import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.cmd.*;
import org.pageflow.common.result.Result;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocNodeUseCase {
  Result relocateNode(UUID bookId, RelocateNodeCmd cmd);

  FolderDto createFolder(UUID bookId, CreateFolderCmd cmd);
  FolderDto queryFolder(UUID bookId, UUID folderId);
  FolderDto updateFolder(UUID bookId, UpdateFolderCmd cmd);
  void deleteFolder(UUID bookId, UUID folderId);

  SectionDtoWithContent createSection(UUID bookId, CreateSectionCmd cmd);
  SectionDto querySection(UUID bookId, UUID sectionId);
  SectionDtoWithContent querySectionWithContent(UUID bookId, UUID sectionId);
  SectionDtoWithContent updateSection(UUID bookId, UpdateSectionCmd cmd);
  void deleteSection(UUID bookId, UUID sectionId);
}
