package org.pageflow.test.module.book.utils;

import java.util.function.Consumer;

/**
 * @author : sechan
 */
public interface TocTreeBuilderFolder {
  TocTreeBuilderFolder folder(String title, Consumer<TocTreeBuilderFolder> folderConsumer);

  TocTreeBuilderFolder folder(String title);

  TocTreeBuilderFolder section(String title);
}
