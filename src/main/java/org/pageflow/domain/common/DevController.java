package org.pageflow.domain.common;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class DevController {
    
    private final Rq rq;
    private final FileService fileService;
    
    @GetMapping("/application")
    public String applicationPage() {
        return "/user/application/application";
    }
    
    @PostMapping("/file/upload")
    @ResponseBody
    public String fileUpload(@RequestPart MultipartFile profileImg) {
        fileService.uploadFile(profileImg, rq.getAccount().getProfile(), FileMetadataType.PROFILE_IMG);
        return "/user/application/application";
    }
    
}
