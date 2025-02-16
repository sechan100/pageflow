package org.pageflow.file.shared;

import lombok.Getter;

/**
 * @author : sechan
 */
public interface FileType {
  FileOwnerType getOwnerType();

  String name();

  enum USER implements FileType {
    PROFILE_IMAGE;

    @Getter
    private final FileOwnerType ownerType = FileOwnerType.USER;
  }

}

