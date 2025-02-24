package org.pageflow.book.port.in;

import org.pageflow.book.dto.TocDto;

/**
 * @author : sechan
 */
public interface TocUseCase {
  void replaceNode(BookPermission permission, ReplaceNodeCmd cmd);
  TocDto.Toc loadToc(BookPermission permission);
}
