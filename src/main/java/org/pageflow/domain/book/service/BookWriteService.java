package org.pageflow.domain.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransientPropertyValueException;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.outline.ChapterSummary;
import org.pageflow.domain.book.model.outline.Outline;
import org.pageflow.domain.book.model.outline.PageSummary;
import org.pageflow.domain.book.model.request.BookUpdateRequest;
import org.pageflow.domain.book.model.request.PageUpdateRequest;
import org.pageflow.domain.book.model.request.RearrangeRequest;
import org.pageflow.domain.book.repository.ChapterRepository;
import org.pageflow.domain.book.repository.PageRepository;
import org.pageflow.domain.user.entity.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookWriteService {
    
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final BookService bookService;
    private final SelectiveLISOptimizer selectiveLISOptimizer;
    
    /**
     * @return 새로운 책 객체를 반환한다. 작성이 되지 않은 책과, 하나씩의 기본 챕터와 페이지를 가진다.
     */
    @Transactional
    public Book createBlankBook(Profile author) {
        
        // pp: 프로젝트 디렉토리에 기본 커버 이미지 저장하고 그 경로를 줘야함.
        String defaultCoverImgUrl = "https://library.kbu.ac.kr/libeka/fileview/3025aced-3e0a-4266-86ed-a1894eb759b3.JPG";
        
        Book newBook = Book.builder()
                .title("제목을 입력해주세요")
                .isPublished(false)
                .coverImgUrl(defaultCoverImgUrl)
                .author(author)
                .build();
        
        // 책 먼저 영속
        Book persistedBook = bookService.delegateSave(newBook);
        
        // 기본 챕터와 기본 페이지 생성
        Book persistedBookWithDefaultChapterAndDefualtPage = createBlankChapter(persistedBook);
        
        Book book11 = createBlankChapter(persistedBookWithDefaultChapterAndDefualtPage);
        Book b111 = createBlankChapter(book11);
        b111.getChapters().set(0, createBlankPage(b111.getChapters().get(0)));
        b111.getChapters().set(0, createBlankPage(b111.getChapters().get(0)));
        b111.getChapters().set(0, createBlankPage(b111.getChapters().get(0)));
        b111.getChapters().set(1, createBlankPage(b111.getChapters().get(1)));
        b111.getChapters().set(1, createBlankPage(b111.getChapters().get(1)));
        
        return persistedBookWithDefaultChapterAndDefualtPage;
    }
    
    
    /**
     * 새로운 기본 챕터와 기본 페이지를 생성하여 영속후 반환한다.
     * @param ownerBook 새로운 챕터가 소속될 책
     * @return 새로운 챕터가 추가된 책
     *
     * @throws TransientPropertyValueException 소속될 책이 영속 상태가 아닌경우 발생
     */
    @Transactional
    public Book createBlankChapter(Book ownerBook) throws TransientPropertyValueException {
        
        if(ownerBook.getId() == null){
            throw new IllegalArgumentException("챕터를 생성할 책이 지정되지 않았습니다.");
        }
        
        Chapter defaultChapter = Chapter.builder()
                .title("제목을 입력해주세요")
                .book(ownerBook)
                .build();
        
        try {
            // 챕터 영속
            Chapter persistedChapter = chapterRepository.save(defaultChapter);
            
            // 챕터에 기본 페이지 추가
            Chapter persistedChapterWithDefaultPage = createBlankPage(persistedChapter);
            
            // 책에 생성된 챕터추가
            ownerBook.getChapters().add(persistedChapterWithDefaultPage);
            
            return ownerBook;
            
        } catch(TransientPropertyValueException e) {
            log.error("영속상태가 아닌 Book:'{}'(id={})에 새로운 챕터를 생성할 수 없습니다.", ownerBook.getTitle(), ownerBook.getId());
            throw e;
        }
    }
    
    
    /**
     * 매개받은 챕터의 소속으로 제일 뒤에 새로운 페이지를 생성한다.
     * @param ownerChapter 새로운 페이지를 생성할 챕터
     * @return 새로운 페이지가 추가된 챕터
     *
     * @throws TransientPropertyValueException 소속될 챕터가 영속 상태가 아닌경우 발생
     */
    @Transactional
    public Chapter createBlankPage(Chapter ownerChapter) throws TransientPropertyValueException {
        
        Page newDefaultPage = Page.builder()
                .chapter(ownerChapter)
                .title("새 페이지")
                .content("내용을 입력하세요")
                .build();
        
        try {
            // 페이지 영속
            Page persistedPage = pageRepository.save(newDefaultPage);
            
            // 챕터에 페이지 추가
            ownerChapter.getPages().add(persistedPage);
            
            return ownerChapter;
            
        } catch(TransientPropertyValueException e) {
            log.error("영속상태가 아닌 Chapter:'{}'(id={})에 새로운 페이지를 생성할 수 없습니다.", ownerChapter.getTitle(), ownerChapter.getId());
            throw e;
        }
    }
    
    
    /**
     * @param updateRequest 업데이트 변경사항 dto
     * @return update된 책.
     */
    @Transactional
    public Book updateBook(BookUpdateRequest updateRequest) {
        
        if(updateRequest.getId() == null){
            throw new IllegalArgumentException("업데이트의 대상인 Book 엔티티를 특정할 수 없습니다.");
        }
        
        Book staleBook = bookService.delegateFindBookWithAuthorById(updateRequest.getId());
        staleBook.setTitle(updateRequest.getTitle());
        staleBook.setCoverImgUrl(updateRequest.getCoverImgUrl());
        staleBook.setPublished(updateRequest.isPublished());
        
        // 데이터 커밋
        Book updatedBook = bookService.delegateSave(staleBook);
        
        return updatedBook;
    }
    
    
    /**
     * @param updateRequest 업데이트 변경사항 dto
     * @return update된 Chapter
     */
    @Transactional
    public Chapter updateChapter(RearrangeRequest updateRequest) {
        
        if(updateRequest.getId() == null){
            throw new IllegalArgumentException("업데이트의 대상인 Chapter 엔티티를 특정할 수 없습니다.");
        }
        
        Chapter staleChapter = chapterRepository.findById(updateRequest.getId()).orElseThrow();
        staleChapter.setSortPriority(updateRequest.getSortPriority() != null ? updateRequest.getSortPriority() : staleChapter.getSortPriority());
        
        // 데이터 커밋
        Chapter updatedChapter = chapterRepository.save(staleChapter);
        
        return updatedChapter;
    }
    
    
    /**
     * @param updateRequest 업데이트 변경사항 dto
     * @return update된 Page
     */
    @Transactional
    public Page updatePage(PageUpdateRequest updateRequest) {
        
        if(updateRequest.getId() == null){
            throw new IllegalArgumentException("업데이트의 대상인 Page 엔티티를 특정할 수 없습니다.");
        }
        
        Page stalePage = pageRepository.findById(updateRequest.getId()).orElseThrow();
        stalePage.setTitle(updateRequest.getTitle() != null ? updateRequest.getTitle() : stalePage.getTitle());
        stalePage.setContent(updateRequest.getContent() != null ? updateRequest.getContent() : stalePage.getContent());
        stalePage.setSortPriority(updateRequest.getSortPriority() != null ? updateRequest.getSortPriority() : stalePage.getSortPriority());
        
        // 데이터 커밋
        Page updatedPage = pageRepository.save(stalePage);
        
        return updatedPage;
    }
    
    
    /**
     * Selctive LIS 알고리즘을 통해서 오름차순이 파괴된 수열에서 필요한 항을 제해가면서 가장 긴 오름차순 배열을 추출해낼 수 있다.
     * 이를 통해서 최소한의 엔트리 업데이트를 통해서도 오름차순 정렬을 유지할 수 있다.
     * @param rearrangeRequest 재정렬 변경사항 dto
     * @return update된 Outline
     */
    @Transactional
    public Outline delegateRearrange(Outline rearrangeRequest) {
        
        List<ChapterSummary> chapterSummaries = rearrangeRequest.getChapters();
        
        // 우선순위가 망가진 챕터들의 sortPriority 배열
        List<Integer> staleSortPriorities = chapterSummaries.stream().map(ChapterSummary::getSortPriority).toList();
        // Selective LIS 알고리즘을 적용하여 불연속적일 수 있는 항으로 이루어진 최장 오름차순 부분배열을 추출.
        List<Integer> staleSortPriorityLIS = selectiveLISOptimizer.findSelectiveLIS(staleSortPriorities);
        
        
        
        
        
        List<PageSummary> pageSummaries = rearrangeRequest.getPages();
        

        
        return bookService.getOutline(request.getId());
    }
    
}














