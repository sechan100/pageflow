package org.pageflow.test.module.book.utils;

import org.pageflow.book.application.dto.node.FolderDto;
import org.pageflow.book.application.dto.node.WithContentSectionDto;
import org.pageflow.book.usecase.cmd.CreateFolderCmd;
import org.pageflow.book.usecase.cmd.CreateSectionCmd;

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
      CreateFolderCmd.of(
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
      CreateSectionCmd.of(
        context.getUid(),
        context.getBookId(),
        parentFolderId,
        title
      )
    );
    return this;
  }

  @Override
  public TocTreeBuilderFolder folder(UUID id, Consumer<TocTreeBuilderFolder> folderConsumer) {
    FolderDto folderDto = context.create(
      CreateFolderCmd.withId(
        context.getUid(),
        context.getBookId(),
        parentFolderId,
        id.toString(),
        id
      )
    );

    TocTreeBuilderFolderImpl folder = new TocTreeBuilderFolderImpl(context, folderDto.getId());
    folderConsumer.accept(folder);
    return this;
  }

  @Override
  public TocTreeBuilderFolder folder(UUID id) {
    return folder(id, folder -> {
      // do nothing
    });
  }

  @Override
  public TocTreeBuilderFolder section(UUID id) {
    WithContentSectionDto sectionDto = context.create(
      CreateSectionCmd.withId(
        context.getUid(),
        context.getBookId(),
        parentFolderId,
        id.toString(),
        id
      )
    );
    return this;
  }


}
