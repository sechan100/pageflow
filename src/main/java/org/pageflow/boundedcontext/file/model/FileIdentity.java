package org.pageflow.boundedcontext.file.model;

import com.google.common.base.Preconditions;
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
    FileOwnerType ownerType;
    FileType fileType;

    public FileIdentity(TSID ownerId, FileOwnerType ownerType, FileType fileType) {
        Preconditions.checkState(
            fileType.isAvailableOwner(ownerType),
            "FileOwnerType:%s는 FileType:%s에 대한 소유자로 사용할 수 없습니다.",
            ownerType,
            fileType
        );
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.fileType = fileType;
    }
}
