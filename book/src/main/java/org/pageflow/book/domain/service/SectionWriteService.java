package org.pageflow.book.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookId;
import org.pageflow.book.application.BookPermission;
import org.pageflow.book.domain.entity.Section;
import org.pageflow.book.dto.SectionDto;
import org.pageflow.book.dto.SectionDtoWithContent;
import org.pageflow.book.port.in.LexicalHtmlSectionContent;
import org.pageflow.book.port.in.SectionWriteUseCase;
import org.pageflow.book.port.in.cmd.UpdateSectionCmd;
import org.pageflow.book.port.out.jpa.SectionPersistencePort;
import org.pageflow.common.permission.PermissionRequired;
import org.pageflow.common.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SectionWriteService implements SectionWriteUseCase {
  private final SectionPersistencePort sectionPersistencePort;

  @Override
  @PermissionRequired(
    actions = {"READ"},
    permissionType = BookPermission.class
  )
  public SectionDtoWithContent getSectionWithContent(@BookId UUID bookId, UUID sectionId) {
    Section section = sectionPersistencePort.findById(sectionId).get();
    return SectionDtoWithContent.from(section);
  }

  @Override
  @PermissionRequired(
    actions = {"EDIT"},
    permissionType = BookPermission.class
  )
  public Result<SectionDto> updateSection(@BookId UUID bookId, UpdateSectionCmd cmd) {
    Section section = sectionPersistencePort.findById(cmd.getId()).get();
    section.changeTitle(cmd.getTitle().getValue());
    return Result.success(SectionDto.from(section));
  }

  @Override
  @PermissionRequired(
    actions = {"EDIT"},
    permissionType = BookPermission.class
  )
  public Result<SectionDtoWithContent> writeContent(@BookId UUID bookId, UUID sectionId, LexicalHtmlSectionContent content) {

    return null;
  }
}
