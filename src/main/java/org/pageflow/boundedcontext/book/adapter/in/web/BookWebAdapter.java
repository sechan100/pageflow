package org.pageflow.boundedcontext.book.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.book.application.dto.BookDto;
import org.pageflow.boundedcontext.book.port.in.BookUseCase;
import org.pageflow.boundedcontext.book.port.in.CreateBookCmd;
import org.pageflow.global.api.RequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class BookWebAdapter {
    private final BookUseCase bookUseCase;
    private final RequestContext rqcx;

    @PostMapping("/books")
    @Operation(summary = "책 생성")
    public Res.CreatedBook createBook(@RequestBody Req.CreateBook req) {
        CreateBookCmd cmd = new CreateBookCmd(
            rqcx.getUid(),
            req.getTitle(),
            req.getCoverImageUrl()
        );

        BookDto.Basic result = bookUseCase.createBook(cmd);
        return new Res.CreatedBook(
            result.getId(),
            result.getTitle(),
            result.getCoverImageUrl()
        );
    }
}
