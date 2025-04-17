package org.pageflow.test.module.book.utils;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author : sechan
 */
public interface TocTreeBuilderFolder {
  @Deprecated
  TocTreeBuilderFolder folder(String title, Consumer<TocTreeBuilderFolder> folderConsumer);

  @Deprecated
  TocTreeBuilderFolder folder(String title);

  @Deprecated
  TocTreeBuilderFolder section(String title);

  TocTreeBuilderFolder folder(UUID id, Consumer<TocTreeBuilderFolder> folderConsumer);

  TocTreeBuilderFolder folder(UUID id);

  TocTreeBuilderFolder section(UUID id);
}
