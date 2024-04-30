package org.pageflow.boundedcontext.file.model;

import lombok.Value;
import org.pageflow.boundedcontext.file.shared.FileOwnerType;
import org.pageflow.boundedcontext.file.shared.FileType;
import org.pageflow.shared.type.TSID;

/**
 * 애플리케이션의 관점에서 file이 가지는 정체성을 나타내는 객체
 * (해당 파일이 어떤 도메인 객체의 어떤 유형의 파일인지를 의미)
 * @author : sechan
 */
@Value
public class FileIdentity {
    TSID ownerId;
    FileOwnerType fileOwnerType;
    FileType fileType;

    public FileIdentity(TSID ownerId, FileOwnerType ownerType, FileType fileType) {
        this.ownerId = ownerId;
        this.fileOwnerType = ownerType;
        this.fileType = fileType;
    }
}
