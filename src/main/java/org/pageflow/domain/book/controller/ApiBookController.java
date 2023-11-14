package org.pageflow.domain.book.controller;


import lombok.RequiredArgsConstructor;
import org.pageflow.base.request.Rq;
import org.pageflow.base.response.WithAlertApiResponse;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.outline.Outline;
import org.pageflow.domain.book.model.request.PageUpdateRequest;
import org.pageflow.domain.book.service.BookService;
import org.pageflow.domain.book.service.BookWriteService;
import org.pageflow.domain.user.service.AccountService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Transactional
public class ApiBookController {
    
    private final Rq rq;
    private final BookService bookService;
    private final BookWriteService bookWriteService;
    private final AccountService accountService;
    
    
    
    /**
     * 책 목차정보 조회
     */
    @GetMapping("/api/book/{bookId}/outline")
    public Outline readOutline(
            @PathVariable("bookId") Long bookId
    ) {
        return bookService.getOutline(bookId);
    }
    
    
    /**
     * 새롭게 정렬된 Outline 데이터를 기반으로 서버 데이터를 업데이트하고 반환.
     */
    @PutMapping("/api/book/{bookId}/outline")
    public Outline rearrangeOutlineItems(
            @PathVariable("bookId") Long bookId, @RequestBody Outline rearrangeRequest) {
        return bookWriteService.delegateRearrange(rearrangeRequest);
    }
    
    
    /**
     * 새로운 책 생성
     */
    @PostMapping("/api/book")
    public Book createBook() {
        // 비지니스 요구에 따라, 새 책을 생성하지 못하는 경우를 처리하는 로직 추가 예정(ex. 현재 작성중인 책이 너무 많은경우)
        return bookWriteService.createBlankBook(rq.getAccount().getProfile());
    }
    
    
    /**
     * 새로운 Chapter를 생성하여 반환
     * @param bookId 책 아이디
     * @return 새로 생성된 Chapter
     */
    @PostMapping("/api/book/{bookId}/chapter")
    public Chapter createChapter(@PathVariable("bookId") Long bookId) {
        Book ownerBook = bookService.repoFindBookById(bookId);
        return bookWriteService.createBlankChapter(ownerBook);
    }
    
    /**
     * chapterId에 해당하는 Chapter를 삭제하고, 하위 Page들도 모두 삭제
     * @param chapterId 삭제할 챕터 아이디
     * @return 새로운 Outline과 삭제 성공 여부를 담은 alert
     */
    @DeleteMapping("/api/chapter/{chapterId}")
    public WithAlertApiResponse<Outline> deleteChapter(@PathVariable("chapterId") Long chapterId) {
        Chapter deleteTargetChapter = bookService.repoFindChapterById(chapterId);
        boolean isDeleteSuccess = bookWriteService.deleteChapter(chapterId);
        
        if(isDeleteSuccess) {
            return WithAlertApiResponse.success(String.format("'%s' 챕터를 삭제했습니다.", deleteTargetChapter.getTitle()), bookService.getOutline(deleteTargetChapter.getBook().getId()));
        } else {
            return WithAlertApiResponse.error("챕터를 삭제하지 못했습니다. 잠시후에 다시 시도해주세요.");
        }
    }
    
    /**
     * pageId에 해당하는 Page를 삭제하고 결과가 적용된 Outline을 반환
     * @param pageId 삭제할 Page 아이디
     * @return 새로운 Outline과 삭제 성공 여부를 담은 alert
     */
    @DeleteMapping("/api/page/{pageId}")
    public WithAlertApiResponse<Outline> deletePage(@PathVariable("pageId") Long pageId) {
        Page deleteTargetPage = bookService.repoFindPageById(pageId);
        boolean isDeleteSuccess = bookWriteService.deletePage(pageId);
        
        if(isDeleteSuccess) {
            return WithAlertApiResponse.success(String.format("'%s' 페이지를 삭제했습니다.", deleteTargetPage.getTitle()), bookService.getOutline(deleteTargetPage.getChapter().getBook().getId()));
        } else {
            return WithAlertApiResponse.error("페이지를 삭제하지 못했습니다. 잠시후에 다시 시도해주세요.");
        }
    }
    
    
    /**
     * 책 페이지 정보 수정
     */
    @PutMapping("/api/book/page/{pageId}")
    public void updatePage(
            @PathVariable("pageId") Long pageId, @RequestBody PageUpdateRequest updateRequest
    ) {
        bookWriteService.updatePage(updateRequest);
    }
    
}
