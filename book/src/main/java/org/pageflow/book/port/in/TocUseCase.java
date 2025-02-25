package org.pageflow.book.port.in;

import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.cmd.ReplaceNodeCmd;
import org.pageflow.book.port.in.token.BookContext;

/**
 * @author : sechan
 */
public interface TocUseCase {
  void replaceNode(BookContext context, ReplaceNodeCmd cmd);
  TocDto.Toc loadToc(BookContext context);
}
