package org.pageflow.book.port.in;

import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.RelocateNodeCmd;
import org.pageflow.book.port.in.cmd.UpdateFolderCmd;
import org.pageflow.common.result.Result;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocUseCase {
  TocDto.Toc getToc(UUID bookId);

  Result relocateNode(UUID bookId, RelocateNodeCmd cmd);

  FolderDto createFolder(UUID bookId, CreateFolderCmd cmd);

  FolderDto getFolder(UUID bookId, UUID folderId);

  FolderDto updateFolder(UUID bookId, UpdateFolderCmd cmd);

  void deleteFolder(UUID bookId, UUID folderId);

  SectionDtoWithContent createSection(UUID bookId, CreateSectionCmd cmd);

  SectionDto getSection(UUID bookId, UUID sectionId);

  void deleteSection(UUID bookId, UUID sectionId);
}
