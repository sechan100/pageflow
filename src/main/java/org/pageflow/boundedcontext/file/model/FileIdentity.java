package org.pageflow.boundedcontext.file.model;

import lombok.Value;
import org.pageflow.boundedcontext.file.shared.FileOwnerType;
import org.pageflow.boundedcontext.file.shared.FileType;

import java.util.UUID;

/**
 * 서비스 관점에서 file의 유형과 종류를 식별하기위한 클래스
 *
 * @author : sechan
 */
@Value
public class FileIdentity {
  UUID ownerId;
  FileOwnerType fileOwnerType;
  FileType fileType;

  public FileIdentity(UUID ownerId, FileOwnerType ownerType, FileType fileType) {
    this.ownerId = ownerId;
    this.fileOwnerType = ownerType;
    this.fileType = fileType;
  }
}
