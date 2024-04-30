package org.pageflow.boundedcontext.file.shared;

import lombok.Getter;

/**
 * @author : sechan
 */
public interface FileType {
    FileOwnerType getOwnerType();


    enum USER implements FileType {
        PROFILE_IMAGE
        ;

        @Getter
        private static final FileOwnerType ownerType = FileOwnerType.USER;
    }

}

