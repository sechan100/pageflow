package org.pageflow.book.port.in;

import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.cmd.UpdateSectionCmd;
import org.pageflow.common.result.Result;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface SectionWriteUseCase {
  SectionDtoWithContent getSectionWithContent(UUID bookId, UUID sectionId);

  Result<SectionDto> updateSection(UUID bookId, UpdateSectionCmd cmd);

  Result<SectionDtoWithContent> writeContent(UUID bookId, UUID sectionId, LexicalHtmlSectionContent content);
}
