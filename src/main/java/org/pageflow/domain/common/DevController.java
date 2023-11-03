package org.pageflow.domain.common;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.infra.file.service.FileService;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DevController {
    
    private final Rq rq;
    private final FileService fileService;

    
}
