package org.pageflow.test.module.book.utils;

import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.port.in.cmd.CreateFolderCmd;
import org.pageflow.book.port.in.cmd.CreateSectionCmd;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author : sechan
 */
public class TocTreeBuilderFolderImpl implements TocTreeBuilderFolder {
  private final TocTreeBuildContext context;
  private final UUID parentFolderId;

  public TocTreeBuilderFolderImpl(TocTreeBuildContext context, UUID parentFolderId) {
    this.context = context;
    this.parentFolderId = parentFolderId;
  }

  @Override
  public TocTreeBuilderFolder folder(String title, Consumer<TocTreeBuilderFolder> folderConsumer) {
    FolderDto folderDto = context.create(
      new CreateFolderCmd(
        context.getUid(),
        context.getBookId(),
        parentFolderId,
        title
      )
    );

    TocTreeBuilderFolderImpl folder = new TocTreeBuilderFolderImpl(context, folderDto.getId());
    folderConsumer.accept(folder);
    return this;
  }

  @Override
  public TocTreeBuilderFolder folder(String title) {
    return folder(title, folder -> {
      // do nothing
    });
  }

  @Override
  public TocTreeBuilderFolder section(String title) {
    WithContentSectionDto sectionDto = context.create(
      new CreateSectionCmd(
        context.getUid(),
        context.getBookId(),
        parentFolderId,
        title
      )
    );
    return this;
  }


}
