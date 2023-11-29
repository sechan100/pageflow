package org.pageflow.domain.book.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.annotation.SecuredBookId;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.request.BookUpdateRequest;
import org.pageflow.domain.book.model.request.ChapterUpdateRequest;
import org.pageflow.domain.book.model.request.OutlineUpdateRequest;
import org.pageflow.domain.book.model.request.PageUpdateRequest;
import org.pageflow.domain.book.model.summary.Outline;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.book.service.BookWriteService;
import org.pageflow.domain.user.service.AccountService;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.service.FileService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Transactional
public class ApiBookWriteController {
    
    private final Rq rq;
    private final BookService bookService;
    private final BookWriteService bookWriteService;
    private final AccountService accountService;
    private final FileService fileService;
    
    
    
    /**
     * 책 목차정보 조회
     */
    @GetMapping("/api/books/{bookId}/outline")
    public Outline readOutline(
            @PathVariable("bookId") Long bookId
    ) {
        return bookService.getOutline(bookId);
    }
    
    
    /**
     * 새롭게 정렬된 Outline 데이터를 기반으로 서버 데이터를 업데이트하고 반환.
     * Chapter 재정렬, Page 재정렬, Chapter 삭제, Page 삭제를 처리
     */
    @PutMapping("/api/books/{bookId}/outline")
    public Outline updateOutline(
            @SecuredBookId @PathVariable("bookId") Long bookId,
            @Valid @RequestBody OutlineUpdateRequest outlineUpdateRequest
    ) {
        outlineUpdateRequest.setId(bookId);
        
        bookWriteService.delegateDeleteRearrangeable(outlineUpdateRequest);
        
        return bookWriteService.delegateRearrange(outlineUpdateRequest);
    }
    
    
    /**
     * 새로운 책 생성
     */
    @PostMapping("/api/books")
    public Book createBook() {
        // 비지니스 요구에 따라, 새 책을 생성하지 못하는 경우를 처리하는 로직 추가 예정(ex. 현재 작성중인 책이 너무 많은경우)
        return bookWriteService.createBlankBook(rq.getAccount().getProfile());
    }
    
    
    /**
     * 책 정보 업데이트
     * isPublished는 수정하지 않음
     */
    @PutMapping("/api/books/{bookId}")
    public Map<String, String> updateBook(
            @SecuredBookId @PathVariable("bookId") Long bookId,
            @ModelAttribute BookUpdateRequest updateRequest
    ) {
        
        if(updateRequest.getId() == null){
            updateRequest.setId(bookId);
        }
        
        Book updatedBook = bookWriteService.updateBook(updateRequest);
        
        return Map.of(
                "id", updatedBook.getId().toString(),
                "title", updatedBook.getTitle(),
                "coverImgUrl", updatedBook.getCoverImgUrl()
        );
        
    }
    
    
    /**
     * 새로운 Chapter를 생성하여 반환
     * @param bookId 책 아이디
     * @return 새로 생성된 Chapter
     */
    @PostMapping("/api/books/{bookId}/chapters")
    public Chapter createChapter(@SecuredBookId @PathVariable("bookId") Long bookId) {
            return bookWriteService.createBlankChapter(bookId);
    }
    
    
    /**
     * 챕터 정보 업데이트
     */
    @PutMapping("/api/books/{bookId}/chapters")
    public List<Map<String, String>> updateChapters(
            @SecuredBookId @PathVariable("bookId") Long bookId,
            @Valid @RequestBody List<ChapterUpdateRequest> updateRequests
    ) {

        List<Chapter> updatedChapters = bookWriteService.updateChapters(updateRequests);
        
        return updatedChapters.stream()
                .map(chapter -> Map.of("id", chapter.getId().toString(), "title", chapter.getTitle()))
                .toList();
    }
    
    
    /**
     * 새로운 Page를 생성하여 반환
     * @param chapterId 챕터 아이디
     * @return 새로 생성된 Page
     */
    @PostMapping("/api/books/{bookId}/chapters/{chapterId}/pages")
    public Page createPage(
            @SecuredBookId @PathVariable("bookId") Long bookId,
            @PathVariable("chapterId") Long chapterId)
    {
        Chapter ownerChapter = bookService.repoFindChapterById(chapterId);
        return bookWriteService.createBlankPage(ownerChapter);
    }
    
    /**
     * @param bookId 책 아이디
     * @param pageId 페이지 아이디
     * @return 책 페이지 정보
     */
    @GetMapping("/api/books/{bookId}/chapters/pages/{pageId}")
    public Page readPage(
            @SecuredBookId @PathVariable("bookId") Long bookId,
            @PathVariable("pageId") Long pageId
    ) {
        return bookService.repoFindPageById(pageId);
    }
    
    /**
     * 책 페이지 정보 수정
     */
    @PutMapping("/api/books/{bookId}/chapters/pages")
    public List<Page> updatePage(
            @SecuredBookId @PathVariable("bookId") Long bookId,
            @Valid @RequestBody List<PageUpdateRequest> updateRequests
    ) {
        return bookWriteService.updatePages(updateRequests);
    }
    
    
    @PostMapping("/api/books/{bookId}/chapters/pages/{pageId}/imgs")
    public String uploadPageImg(
            @ModelAttribute MultipartFile imgFile,
            @SecuredBookId @PathVariable("bookId") Long bookId,
            @PathVariable("pageId") Long pageId
    ) {
        if(imgFile == null){
            throw new IllegalArgumentException("이미지 파일이 없습니다");
        }경
    
        Page ownerPage = bookService.repoFindPageById(pageId);
        FileMetadata imgMetadata = fileService.uploadFile(imgFile, ownerPage, FileMetadataType.PAGE_IMG);
        
        return fileService.getImgUri(imgMetadata);
    }
    
}
