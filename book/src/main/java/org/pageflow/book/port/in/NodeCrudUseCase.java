package org.pageflow.book.port.in;

import org.pageflow.book.dto.FolderDto;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;
import org.pageflow.book.port.in.cmd.UpdateFolderCmd;
import org.pageflow.book.port.in.cmd.UpdateSectionCmd;
import org.pageflow.book.port.in.token.BookContext;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface NodeCrudUseCase {
  FolderDto createFolder(BookContext context, CreateFolderCmd cmd);
  FolderDto queryFolder(BookContext context, UUID folderId);
  FolderDto updateFolder(BookContext context, UpdateFolderCmd cmd);
  void deleteFolder(BookContext context, UUID folderId);

  SectionDtoWithContent createSection(BookContext context, CreateSectionCmd cmd);
  SectionDto querySection(BookContext context, UUID sectionId);
  SectionDtoWithContent querySectionWithContent(BookContext context, UUID sectionId);
  SectionDtoWithContent updateSection(BookContext context, UpdateSectionCmd cmd);
  void deleteSection(BookContext context, UUID sectionId);
}
