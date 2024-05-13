package org.pageflow.boundedcontext.user.port.in;

import org.pageflow.boundedcontext.common.exception.InputValueException;
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
            throw InputValueException.builder()
                .message("첨부된 파일이 없습니다.")
                .field("file", null)
                .build();
        }
        if(file.getOriginalFilename() == null){
            throw InputValueException.builder()
                .message("파일 이름이 없습니다.")
                .field("filename", null)
                .build();
        }
    }

    @Override
    public String toString() {
        return value.getName();
    }
}
