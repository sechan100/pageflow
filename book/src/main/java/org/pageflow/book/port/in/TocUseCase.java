package org.pageflow.book.port.in;

import org.pageflow.book.dto.TocDto;
import org.pageflow.book.port.in.cmd.ReplaceNodeCmd;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface TocUseCase {
  void replaceNode(UUID bookId, ReplaceNodeCmd cmd);
  TocDto.Toc loadToc(UUID bookId);
}
