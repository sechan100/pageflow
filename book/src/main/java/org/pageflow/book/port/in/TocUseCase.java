package org.pageflow.book.port.in;

import org.pageflow.book.dto.TocDto;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocUseCase {
  void replaceNode(ReplaceNodeCmd cmd);
  TocDto.Toc loadToc(UUID bookId);
}
