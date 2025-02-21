package org.pageflow.book.port.in;

import org.pageflow.book.dto.*;
import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookQueries {
  BookDtoWithAuthor queryBook(UUID id);

  MyBooks queryMyBooks(UID uid);

  FolderDto queryFolder(UUID folderId);

  SectionDto querySection(UUID sectionId);
  SectionDtoWithContent querySectionWithContent(UUID sectionId);
}
