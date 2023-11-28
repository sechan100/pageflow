package org.pageflow.domain.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransientPropertyValueException;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.domain.book.constants.BookStatus;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.request.BookUpdateRequest;
import org.pageflow.domain.book.model.request.ChapterUpdateRequest;
import org.pageflow.domain.book.model.request.OutlineUpdateRequest;
import org.pageflow.domain.book.model.request.PageUpdateRequest;
import org.pageflow.domain.book.model.summary.ChapterSummary;
import org.pageflow.domain.book.model.summary.Outline;
import org.pageflow.domain.book.model.summary.PageSummary;
import org.pageflow.domain.book.model.summary.Rearrangeable;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookWriteService {
    
    private final BookService bookService;
    private final FileService fileService;
    private final CustomProperties customProperties;
    private final SelectiveLISOptimizer selectiveLISOptimizer;
    
    /**
     * @return 새로운 책 객체를 반환한다. 작성이 되지 않은 책과, 하나씩의 기본 챕터와 페이지를 가진다.
     */
    @Transactional
    public Book createBlankBook(Profile author) {
        
        // pp: 프로젝트 디렉토리에 기본 커버 이미지 저장하고 그 경로를 줘야함.
        String defaultCoverImgUrl = customProperties.getDefaults().getDefaultBookCoverImg();
        
        Book newBook = Book.builder()
                .title("제목을 입력해주세요")
                .status(BookStatus.DRAFT)
                .coverImgUrl(defaultCoverImgUrl)
                .author(author)
                .build();
        
        // 책 먼저 영속
        Book persistedBook = bookService.repoSaveBook(newBook);
        
        // 기본 챕터와 기본 페이지 생성
        Chapter defaultChapterWithDefaultPages = createBlankChapter(persistedBook);

        
        return persistedBook;
    }
    
    
    /**
     * 매개받은 책의 소속으로 제일 뒤에 새로운 챕터를 생성한다.
     * 추가로, 매개로 받은 Book 엔티티의 Chapters 컬렉션의 참조를 통해서 생성된 Chapter를 추가한다.
     * (영속성 컨텍스트의 1차 캐시를 초기화할 필요없이 바로 객체 참조를 통해서 새로 생성된 Chapter를 사용가능하다.)
     *
     * @param ownerBook 새로운 Chapter를 생성할 Book
     * @return 새로 생성된 Chapter
     *
     * @throws TransientPropertyValueException 소속될 챕터가 영속 상태가 아닌경우 발생
     */
    @Transactional
    public Chapter createBlankChapter(Book ownerBook) throws TransientPropertyValueException {
        
        if(ownerBook.getId() == null){
            throw new IllegalArgumentException("챕터를 생성할 책이 지정되지 않았습니다.");
        }
        
        Chapter defaultChapter = Chapter.builder()
                .title("제목을 입력해주세요")
                .book(ownerBook)
                .build();
        
        try {
            // 챕터 영속
            Chapter persistedChapter = bookService.repoSaveChapter(defaultChapter);
            
            // 챕터에 기본 페이지 추가
            Page defaultPage = createBlankPage(persistedChapter);
            
            // 책에 생성된 챕터추가
            ownerBook.getChapters().add(persistedChapter);
            
            return persistedChapter;
            
        } catch(TransientPropertyValueException e) {
            log.error("영속상태가 아닌 Book:'{}'(id={})에 새로운 챕터를 생성할 수 없습니다.", ownerBook.getTitle(), ownerBook.getId());
            throw e;
        }
    }
    
    @Transactional
    public Chapter createBlankChapter(Long bookId) throws TransientPropertyValueException, NoSuchElementException {
        return createBlankChapter(bookService.repoFindBookById(bookId));
    }
    
    @Transactional
    public boolean deleteChapter(Long chapterId) {
            
            Chapter chapterToDelete = bookService.repoFindChapterById(chapterId);
            
            try {
                // Chapter 삭제 + 고아객체 삭제가 ON이므로, 참조를 잃은 Page들을 자동으로 같이 삭제
                bookService.repoDeleteChapter(chapterToDelete);
                return true;
            } catch(Exception e) {
                log.error("챕터 삭제 중 오류가 발생했습니다." + e.getMessage());
                return false;
            }
    }
    
    /**
     * 매개받은 챕터의 소속으로 제일 뒤에 새로운 페이지를 생성한다.
     * 추가로, 매개로 받은 Chapter 엔티티의 Pages 컬렉션의 참조를 통해서 생성된 페이지를 추가한다.
     * (영속성 컨텍스트의 1차 캐시를 초기화할 필요없이 바로 객체 참조를 통해서 새로 생성된 Page를 사용가능하다.)
     *
     * @param ownerChapter 새로운 페이지를 생성할 챕터
     * @return 새로 생성된 Page
     *
     * @throws TransientPropertyValueException 소속될 챕터가 영속 상태가 아닌경우 발생
     */
    @Transactional
    public Page createBlankPage(Chapter ownerChapter) throws TransientPropertyValueException {
        
        Page newDefaultPage = Page.builder()
                .chapter(ownerChapter)
                .title("새 페이지")
                .content("내용을 입력하세요")
                .build();
        
        try {
            // 페이지 영속
            Page persistedPage = bookService.repoSavePage(newDefaultPage);
            
            // 챕터에 페이지 추가
            ownerChapter.getPages().add(persistedPage);
            
            return persistedPage;
            
        } catch(TransientPropertyValueException e) {
            log.error("영속상태가 아닌 Chapter:'{}'(id={})에 새로운 페이지를 생성할 수 없습니다.", ownerChapter.getTitle(), ownerChapter.getId());
            throw e;
        }
    }
    
    
    @Transactional
    public boolean deletePage(Long pageId) {
        
        Page pageToDelete = bookService.repoFindPageById(pageId);
        
        try {
            // page 삭제
            bookService.repoDeletePage(pageToDelete);
            return true;
        } catch(Exception e) {
            log.error("페이지 삭제 중 오류가 발생했습니다." + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * @param updateRequest 업데이트 변경사항 dto
     * @return update된 책.
     */
    @Transactional
    public Book updateBook(BookUpdateRequest updateRequest) {
        
        // 유효성 검사
        if(updateRequest.getId() == null){
            throw new IllegalArgumentException("업데이트의 대상인 Book 엔티티를 특정할 수 없습니다.");
        }
        
        // 업데이트의 대상인 Book 데이터를 가져옴
        Book staleBook = bookService.repoFindBookWithAuthorById(updateRequest.getId());
        
        /* BookUpdateRequest에 등록할 새로운 coverImg가 존재 && 기존의 이미지가 기본 커버 이미지가 아닌 경우
         * -> 기존의 coverImg를 삭제후 새로운 이미지 등록
         */
        if(updateRequest.getCoverImg() != null){
            
            // 기존 이미지가 default 이미지가 아니라면 삭제로직 실행
            if(!staleBook.getCoverImgUrl().equals(customProperties.getDefaults().getDefaultBookCoverImg())){
                fileService.deleteFile(fileService.getPureFilePath(staleBook.getCoverImgUrl()));
            }
            
            // 새로운 이미지 등록 후 저장
            FileMetadata newCoverImg = fileService.uploadFile(updateRequest.getCoverImg(), staleBook, FileMetadataType.BOOK_COVER_IMG);
            staleBook.setCoverImgUrl(fileService.getImgUri(newCoverImg));
        }
        
        // 제목 업데이트
        staleBook.setTitle(updateRequest.getTitle());
        
        // 데이터 커밋
        return bookService.repoSaveBook(staleBook);
    }
    
    
    /**
     * @param updateRequest 업데이트 변경사항 dto
     * @return update된 Chapter
     */
    @Transactional
    public Chapter updateChapter(ChapterUpdateRequest updateRequest) {
        
        if(updateRequest.getId() == null){
            throw new IllegalArgumentException("업데이트의 대상인 Chapter 엔티티를 특정할 수 없습니다.");
        }
        
        Chapter staleChapter = bookService.repoFindChapterById(updateRequest.getId());
        staleChapter.setTitle(updateRequest.getTitle() != null ? updateRequest.getTitle() : staleChapter.getTitle());
        
        // 데이터 커밋
        return bookService.repoSaveChapter(staleChapter);
    }
    
    
    /**
     * 여러개 업데이트
     * @param updateRequests 업데이트 변경사항 dto 리스트
     * @return update된 Chapter 리스트
     */
    @Transactional
    public List<Chapter> updateChapters(List<ChapterUpdateRequest> updateRequests) {
        
        List<Chapter> updatedChapters = new ArrayList<>();
        
        updateRequests.forEach(chapter -> updatedChapters.add(updateChapter(chapter)));
        
        return updatedChapters;
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
        
        Page stalePage = bookService.repoFindPageById(updateRequest.getId());
        stalePage.setTitle(updateRequest.getTitle() != null ? updateRequest.getTitle() : stalePage.getTitle());
        stalePage.setContent(updateRequest.getContent() != null ? updateRequest.getContent() : stalePage.getContent());
        stalePage.setSortPriority(updateRequest.getSortPriority() != null ? updateRequest.getSortPriority() : stalePage.getSortPriority());
        
        // 데이터 커밋
        
        return bookService.repoSavePage(stalePage);
    }
    
    
    /**
     * Selctive LIS 알고리즘을 통해서 오름차순이 파괴된 수열에서 필요한 항을 제해가면서 가장 긴 오름차순 배열을 추출해낼 수 있다.
     * 이를 통해서 최소한의 엔트리 업데이트를 통해서도 오름차순 정렬을 유지할 수 있다.
     * @param outlineUpdateRequest 재정렬 변경사항 dto
     * @return update된 Outline
     */
    @Transactional
    public Outline delegateRearrange(OutlineUpdateRequest outlineUpdateRequest) {
        
        List<ChapterSummary> chapterSummaries = outlineUpdateRequest.getChapters();

        // chapter sortPriority 업데이트
        int updatedEntityChapter = allocateNewSortPriorityOptimally(chapterSummaries);
        
        // page들의 sortPriority 업데이트
        chapterSummaries.forEach(chapterSummary -> {
            allocateNewOwnerChapterId(chapterSummary.getPages(), chapterSummary.getId()); // 소속 Chapter가 바뀐 Page가 있다면 해당 스트림에서 업데이트 예약됨.
            allocateNewSortPriorityOptimally(chapterSummary.getPages());
        });
        
        return bookService.getOutline(outlineUpdateRequest.getId());
    }
    
    @Transactional
    public void delegateDeleteRearrangeable(OutlineUpdateRequest outlineUpdateRequest) {
        // 삭제해야하는 Chapter, Page의 id들을 추출
        // { "chapter": set[Long], "page": set[Long] }
        Map<String, Set<Long>> deleteTargets = detectDeleteTargets(bookService.getOutline(outlineUpdateRequest.getId()), outlineUpdateRequest);
        
        deleteTargets.get("chapter").forEach(this::deleteChapter);
        deleteTargets.get("page").forEach(this::deletePage);
    }
    
    
    /**
     * @param rearrangeables 재정렬이 필요한 Rearrangeable 리스트
     * @return update된 엔티티의 개수.
     */
    @Transactional
    private <T extends Rearrangeable> int allocateNewSortPriorityOptimally(List<T> rearrangeables){
        
        List<Integer> staleSortPriorities = rearrangeables.stream().map(Rearrangeable::getSortPriority).toList();
        
        // 예를 들어 다른 Chapter에 있던 10000sp인 Page가 다른 챕터로 이동했는데, 거기 이미 10000sp인 Page가 존재한다면, 이는 중복된 sp가 존재하는 경우이다.
        // 이 경우, 새롭게 sp를 할당하는게 더 복잡하고 비효율일 수 있기 때문에, 그냥 바로 rebalancing을 통해 해결한다.
        if(staleSortPriorities.stream().distinct().count() != staleSortPriorities.size()){
            return updateSortPriority(rearrangeables, selectiveLISOptimizer.rebalance(staleSortPriorities));
        }
        
        // Selective LIS 알고리즘을 적용하여 불연속적일 수 있는 항으로 이루어진 최장 오름차순 부분배열을 추출.
        List<Integer> staleSortPriorityLIS = selectiveLISOptimizer.findSelectiveLIS(staleSortPriorities);
        
        if(staleSortPriorities.size() == staleSortPriorityLIS.size()) return 0;
        
        // 새롭게 할당할 sortPriority 리스트
        List<Integer> newAscendingSortPriorityList = getNewAscendingSortPriorityList(staleSortPriorities, staleSortPriorityLIS);
        
        // 만약 newAscendingSortPriorityList에 중복된 값이 존재한다면, 그냥 rebalancing을 진행하여 10000부터 새롭게 시작하는 newAscendingSortPriorityList를 반환한다.
        if(newAscendingSortPriorityList.size() != newAscendingSortPriorityList.stream().distinct().count()){
            newAscendingSortPriorityList = selectiveLISOptimizer.rebalance(newAscendingSortPriorityList);
        }
        
        return updateSortPriority(rearrangeables, newAscendingSortPriorityList);
        
    }
    
    
    /**
     * @param pageSummaries page 리스트
     * @param ownerChapterId outline 상으로 페이지들이 소속된 chapter의 id
     * @return owner chapter가 update된 page 엔티티의 개수
     */
    private int allocateNewOwnerChapterId(List<PageSummary> pageSummaries, Long ownerChapterId){
        AtomicInteger updatedEntity = new AtomicInteger();
        pageSummaries.forEach(pageSummary -> {
            // PageSummary가 필드로 가지고있는 chapterId와 실제 배치되어있는 chapter의 id가 다른 경우
            if(!Objects.equals(pageSummary.getOwnerId(), ownerChapterId)){
                // Page의 ChapterId FK 업데이트
                Page rearrangeableEntity = bookService.repoFindPageById(pageSummary.getId());
                rearrangeableEntity.setChapter(bookService.repoFindChapterById(ownerChapterId));
                updatedEntity.getAndIncrement();
            }
        });
        
        return updatedEntity.get();
    }
    
    
    @Transactional
    private <T extends Rearrangeable> int updateSortPriority(List<T> rearrangeables, List<Integer> newAscendingSortPriorityList){
        AtomicInteger updatedEntityNum = new AtomicInteger();
        for(int i = 0; i < rearrangeables.size(); i++){
            Rearrangeable rearrangeable = rearrangeables.get(i);
            
            // outline에 적힌 sp와 newAscendingSortPriorityList에 적힌 값이 다르면 sortPriority 업데이트가 필요한 chapter임.
            if(!rearrangeable.getSortPriority().equals(newAscendingSortPriorityList.get(i))){
                
                if(rearrangeable instanceof PageSummary stalePage){
                    Page rearrangeableEntity = bookService.repoFindPageById(stalePage.getId());
                    rearrangeableEntity.setSortPriority(newAscendingSortPriorityList.get(i));
                    log.debug("Page {}번의 P가 {} -> {}로 변경되었습니다. 페이지 변경 카운트: {}", rearrangeableEntity.getId(), stalePage.getSortPriority(), newAscendingSortPriorityList.get(i), updatedEntityNum.get() + 1);
                    
                } else if(rearrangeable instanceof ChapterSummary staleChapter){
                    Chapter rearrangeableEntity = bookService.repoFindChapterById(staleChapter.getId());
                    rearrangeableEntity.setSortPriority(newAscendingSortPriorityList.get(i));
                    log.debug("Chapter {}번의 SP가 {} -> {}로 변경되었습니다. 챕터 변경 카운트: {}", rearrangeableEntity.getId(), staleChapter.getSortPriority(), newAscendingSortPriorityList.get(i), updatedEntityNum.get() + 1);
                }
                
                updatedEntityNum.getAndIncrement();
            }
        }
        
        return updatedEntityNum.get();
    }
    
    
    /**
     * @param staleSortPriorities 우선순위가 망가진 챕터들의 sortPriority 배열
     * @param LIS Selective LIS 알고리즘을 적용하여 불연속적일 수 있는 항으로 이루어진 최장 오름차순 부분배열
     * @return 새로운 sortPriority가 필요한 항들에 적절한 값들을 할당한 배열.
     */
    private List<Integer> getNewAscendingSortPriorityList(List<Integer> staleSortPriorities, List<Integer> LIS){
            
        List<Integer> newSortPriorityList = new ArrayList<>();
            
        int staleIdx = 0;
        for(Integer LISValue : LIS) {
            Integer staleSortPriority = staleSortPriorities.get(staleIdx);
            
            if(!staleSortPriority.equals(LISValue)) {
                
                // 현재 LISValue가 staleSortPriorities의 어느 위치에 있는지를 찾는다.
                int LISValueOriginalIdx = staleSortPriorities.indexOf(LISValue);
                // 새로운 sp 할당이 필요한 항의 개수
                int needToAllocateNewSortPriorityNum = LISValueOriginalIdx - staleIdx;
                
                List<Integer> numbers = new ArrayList<>();
                int intervalStartingValue = !newSortPriorityList.isEmpty() ? newSortPriorityList.get(newSortPriorityList.size() - 1) : 0;
                
                // 두 오름차순인 항 사이에 삽입될 새로운 sp들간의 간격 계산
                double interval = (double) (LISValue - intervalStartingValue) / (needToAllocateNewSortPriorityNum + 1);
                
                // n개의 항을 리스트에 추가
                for(int k = 1; k <= needToAllocateNewSortPriorityNum; k++) {
                    newSortPriorityList.add((int) (intervalStartingValue + Math.round(k * interval)));
                }
                
                // LISValue를 리스트에 추가
                newSortPriorityList.add(LISValue);
                staleIdx = staleIdx + needToAllocateNewSortPriorityNum;
                
            } else {
                newSortPriorityList.add(staleSortPriority);
            }
            staleIdx++;
        }
        
        // 기본적인 할당 로직이 끝난 후, 반환해야하는 newSPList의 길이가 staleSPList의 길이보다 짧으면 안된다.
        // 만약 짧다면, LIS가 원본 배열의 앞쪽에 몰려있어서 뒤에는 할당이 잘 안된 것이기 때문에, 뒤에는 10000씩 더해가면서 size가 같아질 때까지 항을 추가한다.
        while(newSortPriorityList.size() < staleSortPriorities.size()) {
            newSortPriorityList.add(newSortPriorityList.get(newSortPriorityList.size() - 1) + 10000);
        }
            
        return newSortPriorityList;
    }
    
    
    /**
     * @return {
     *     "chapter": set[Long],
     *     "page": set[Long]
     *     }
     */
    private Map<String, Set<Long>> detectDeleteTargets(Outline staleOutline, OutlineUpdateRequest outlineUpdateRequest){
        Map<String, Set<Long>> deleteTargets = new HashMap<>();
        
        Set<Long> staleChapterIds = staleOutline.getChapters().stream()
                .map(ChapterSummary::getId)
                .collect(Collectors.toSet());
        
        Set<Long> stalePageIds = staleOutline.getChapters().stream()
                .flatMap(chapterSummary ->
                        chapterSummary.getPages().stream()
                                .map(PageSummary::getId)
                )
                .collect(Collectors.toSet());
        
        Set<Long> newChapterIds = outlineUpdateRequest.getChapters().stream()
                .map(ChapterSummary::getId)
                .collect(Collectors.toSet());
        
        Set<Long> newPageIds = outlineUpdateRequest.getChapters().stream()
                .flatMap(chapterSummary ->
                        chapterSummary.getPages().stream()
                                .map(PageSummary::getId)
                )
                .collect(Collectors.toSet());
        
        deleteTargets.put("chapter", new HashSet<>(staleChapterIds));
        deleteTargets.put("page", new HashSet<>(stalePageIds));
        
        deleteTargets.get("chapter").removeAll(newChapterIds);
        deleteTargets.get("page").removeAll(newPageIds);
        
        return deleteTargets;
    }
    
    
    @Transactional
    public List<Page> updatePages(List<PageUpdateRequest> updateRequests) {
            
        List<Page> updatedPages = new ArrayList<>();
        
        updateRequests.forEach(page -> updatedPages.add(updatePage(page)));
        
        return updatedPages;
    }
}

