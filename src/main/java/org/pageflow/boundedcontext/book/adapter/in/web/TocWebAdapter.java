package org.pageflow.boundedcontext.book.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.port.in.CreateFolderCmd;
import org.pageflow.boundedcontext.book.port.in.TocUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class TocWebAdapter {
    private final TocUseCase tocUseCase;

    @PostMapping
    @Operation
    public void createFolder() {
        CreateFolderCmd cmd = new CreateFolderCmd();
        tocUseCase.createFolder();
    }
}
