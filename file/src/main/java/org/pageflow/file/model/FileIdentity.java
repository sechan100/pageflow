package org.pageflow.file.model;

import lombok.Value;
import org.pageflow.file.shared.FileOwnerType;
import org.pageflow.file.shared.FileType;

/**
 * 서비스 관점에서 file의 유형과 종류를 식별하기위한 클래스
 *
 * @author : sechan
 */
@Value
public class FileIdentity {
  String ownerId;
  FileOwnerType fileOwnerType;
  FileType fileType;

  public FileIdentity(String ownerId, FileOwnerType ownerType, FileType fileType) {
    this.ownerId = ownerId;
    this.fileOwnerType = ownerType;
    this.fileType = fileType;
  }
}
