package org.pageflow.boundedcontext.file.shared;


/**
 * @author : sechan
 */
public enum FileType {
    PROFILE_IMAGE(FileOwnerType.USER),
    ;


    private final FileOwnerType[] availableOwnerTypes;

    FileType(FileOwnerType... ownerTypes) {
        this.availableOwnerTypes = ownerTypes;
    }

    public boolean isAvailableOwner(FileOwnerType ownerType) {
        for (FileOwnerType availableOwnerType : availableOwnerTypes) {
            if (availableOwnerType == ownerType) {
                return true;
            }
        }
        return false;
    }
}
