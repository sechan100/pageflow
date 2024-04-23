package org.pageflow.boundedcontext.file.model;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.pageflow.boundedcontext.file.shared.FileOwnerType;
import org.pageflow.boundedcontext.file.shared.FileType;
import org.pageflow.shared.type.TSID;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
@Value
public class UploadCmd {
    FileIdentity fileIdentity;
    MultipartFile file;

    public UploadCmd(TSID ownerId, FileOwnerType ownerType, FileType fileType, MultipartFile file) {
        assert file != null;
        Preconditions.checkState(
            !file.isEmpty(),
            "업로드할 파일이 비어있습니다."
        );
        Preconditions.checkNotNull(
            file.getOriginalFilename(),
            "파일 이름이 없습니다."
        );
        this.fileIdentity = new FileIdentity(ownerId, ownerType, fileType);
        this.file = file;
    }
}
