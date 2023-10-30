package org.pageflow.domain.common;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.infra.file.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DevController {
    
    private final Rq rq;
    private final FileService fileService;
    
    @GetMapping("/application")
    public String applicationPage() {
        return "/user/application/application";
    }
    
}
