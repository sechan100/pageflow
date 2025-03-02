package org.pageflow.book.port.in;

import org.pageflow.book.dto.TocDto;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocUseCase {
  TocDto.Toc loadToc(UUID bookId);
}
