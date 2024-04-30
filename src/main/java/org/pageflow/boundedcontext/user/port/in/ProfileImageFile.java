package org.pageflow.boundedcontext.user.port.in;

import org.pageflow.global.api.code.Code4;
import org.pageflow.shared.type.SingleValueWrapper;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : sechan
 */
public class ProfileImageFile extends SingleValueWrapper<MultipartFile> {
    
    private ProfileImageFile(MultipartFile value) {
        super(value);
    }

    public static ProfileImageFile of(MultipartFile value) {
        validate(value);
        return new ProfileImageFile(value);
    }

    public static void validate(MultipartFile file) {
        if(file.isEmpty()){
            throw Code4.INVALID_FILE.feedback("파일이 없습니다.");
        }
        if(file.getOriginalFilename() == null){
            throw Code4.INVALID_FILE.feedback("파일 이름이 없습니다.");
        }
    }

    @Override
    public String toString() {
        return value.getName();
    }
}
