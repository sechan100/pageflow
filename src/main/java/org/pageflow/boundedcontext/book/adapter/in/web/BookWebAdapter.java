package org.pageflow.boundedcontext.book.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.dto.*;
import org.pageflow.boundedcontext.book.port.in.BookCreateCmd;
import org.pageflow.boundedcontext.book.port.in.BookQueries;
import org.pageflow.boundedcontext.book.port.in.BookUseCase;
import org.pageflow.boundedcontext.book.port.in.TocUseCase;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.global.api.RequestContext;
import org.pageflow.shared.annotation.Get;
import org.pageflow.shared.annotation.Post;
import org.pageflow.shared.type.TSID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : sechan
 */
@RestController
@RequiredArgsConstructor
public class BookWebAdapter {
    private final BookUseCase bookUseCase;
    private final TocUseCase tocUsecase;

    private final BookQueries bookQueries;
    private final RequestContext rqcx;


    @Post("/user/books")
    @Operation(summary = "책 생성")
    public BookDto.Basic createBook(@RequestBody CreateBook req) {
        BookCreateCmd cmd = new BookCreateCmd(
            rqcx.getUid(),
            req.getTitle(),
            req.getCoverImageUrl()
        );
        BookDto.Basic result = bookUseCase.createBook(cmd);
        return result;
    }
    @Data
    public static class CreateBook {
        private String title;
        private String coverImageUrl;
    }


    @Get("/user/books")
    @Operation(summary = "내 책장 조회")
    public MyBooks getMyBooks() {
        var booksAndAuthor = bookQueries.queryBooksByAuthorId(UID.from(rqcx.getUid()));
        return new MyBooks(
            booksAndAuthor._1,
            booksAndAuthor._2
        );
    }
    @Value
    public static class MyBooks {
        AuthorDto author;
        List<BookDto.Basic> books;
    }


    @Get("/user/books/{bookId}")
    @Operation(summary = "책 조회")
    public BookDto.Basic getBook(@PathVariable TSID bookId) {
        BookDto.Basic book = bookQueries.queryBook(BookId.from(bookId));
        return book;
    }


    @Get("/user/books/{bookId}/toc")
    @Operation(summary = "책 목차 조회")
    public TocDto.Toc getToc(@PathVariable TSID bookId) {
        TocDto.Toc toc = tocUsecase.queryToc(BookId.from(bookId));
        return toc;
    }

    @Get("/user/books/{bookId}/folders/{folderId}")
    @Operation(summary = "폴더 조회")
    public FolderDto.Basic getFolder(@PathVariable TSID bookId, @PathVariable TSID folderId) {
        FolderDto.Basic folder = bookQueries.queryFolder(NodeId.from(folderId));
        return folder;
    }

    @Get("/user/books/{bookId}/sections/{sectionId}")
    @Operation(summary = "섹션 조회")
    public SectionDto.WithContent getSectionWithContent(@PathVariable TSID bookId, @PathVariable TSID sectionId) {
        SectionDto.WithContent section = bookQueries.querySectionWithContent(NodeId.from(sectionId));
        return section;
    }

    @Get("/user/books/{bookId}/sections/{sectionId}/without-content")
    @Operation(summary = "섹션을 컨텐츠 데이터 없이 조회")
    public SectionDto.MetaData getSection(@PathVariable TSID bookId, @PathVariable TSID sectionId) {
        SectionDto.MetaData section = bookQueries.querySectionMetadata(NodeId.from(sectionId));
        return section;
    }


}
