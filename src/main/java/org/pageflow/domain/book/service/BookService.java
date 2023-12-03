package org.pageflow.domain.book.service;

import jakarta.persistence.criteria.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.base.exception.nosuchentity.NoSuchEntityException;
import org.pageflow.base.request.Rq;
import org.pageflow.domain.book.entity.*;
import org.pageflow.domain.book.model.summary.BookSummary;
import org.pageflow.domain.book.model.summary.ChapterSummary;
import org.pageflow.domain.book.model.summary.Outline;
import org.pageflow.domain.book.model.summary.PageSummary;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.book.repository.ChapterRepository;
import org.pageflow.domain.book.repository.PageRepository;
import org.pageflow.domain.interaction.service.PreferenceService;
import org.pageflow.domain.interaction.service.InteractionService;
import org.pageflow.domain.user.entity.Profile;
import org.pageflow.infra.file.service.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
    private final Rq rq;
    private final FileService fileService;
    private final InteractionService interactionService;
    private final PreferenceService preferenceService;

    private Specification<Book> search(String kw) {
        return new Specification<>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(@NonNull Root<Book> b, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
                // b - 기준을 의미하는 Book 앤티티의 객체(책 제목 검색)

                query.distinct(true); //중복 제거

                Join<Book, Profile> u1 = b.join("author", JoinType.LEFT);
                // u1-Book 엔티티와 Account 엔티티를 아우터 조인하여 만든 Account 앤티티 객체.
                // Book 앤티티와 Account 앤티티는 author 속성으로 연결되어 있기 때문에
                // 질문 작성자 검색
                return cb.or(cb.like(b.get("title"), "%" + kw + "%"),
                        cb.like(u1.get("nickname"), "%" + kw + "%"));
            }
        };
    }

    public Slice<BookSummary> getList(int page, String kw, String sortOption) {
        Pageable pageable;

        Specification<Book> spec = search(kw);

        //'좋아요'순 정렬을 선택했을 경우의 처리
        if("preferenceCount".equals(sortOption)) {
            //'좋아요' 순으로 정렬하기 위해 Pageable을 구성.
            //이 경우 'preferenceCount'는 실제 필드가 아니므로, Sort.by를 사용하지 않음.
            pageable = PageRequest.of(page, 20);
            Slice<BookWithPreferenceCount> bookWithPrefs = bookRepository.findAllBooksOrderByPreferenceCount(spec, pageable);
            return bookWithPrefs.map(bwp -> new BookSummary(bwp.getBook(), bwp.getPreferenceCount(), null));

        } else if("commentCount".equals(sortOption)) {
            //'댓글' 순으로 정렬하기 위해 Pageable을 구성.
            //이 경우 'commentCount'는 실제 필드가 아니므로, Sort.by를 사용하지 않음.
            pageable = PageRequest.of(page, 20);
            Slice<BookWithCommentCount> bookWithPrefs = bookRepository.findAllBooksOrderByCommentCount(spec, pageable);
            return bookWithPrefs.map(bwp -> new BookSummary(bwp.getBook(),null, bwp.getCommentCount()));

        } else {
            //기존 정렬 옵션에 따른 처리
            pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, sortOption));
            Slice<Book> books = bookRepository.findAll(spec, pageable);
            return books.map(BookSummary::new);
        }
    }

    
    public Outline getOutline(Long bookId) {
        
        // Book 엔티티를 author만 fetch join으로 조회.
        Book book = repoFindBookWithAuthorAndChapterById(bookId);
        
        List<PageSummary> pageSummaries = pageRepository.findAllByChapterIdIn(
                book.getChapters()
                        .stream()
                        .map(BaseEntity::getId)
                        .toList()
        );
        
        // PageSummary를 chapterId를 기준으로 그룹화
        Map<Long, List<PageSummary>> pageSummariesGroupedByChapter = pageSummaries.stream()
                .collect(Collectors.groupingBy(PageSummary::getOwnerId));
        Set<Long> chapterIdSet = pageSummariesGroupedByChapter.keySet();
        
        // Page가 없는 Chapter는 PageSummary로 조회되지 않으므로 따로 Chapter를 넣어줘야한다. 이를 위한 원본 chapter들의 id 리스트.
        List<Long> chapterIdsFromBook = book.getChapters().stream().map(Chapter::getId).toList();
        
        // pageSummariesGroupedByChapter의 keySet에 없는 chapterId가 있다면, List<PageSummary>를 null로 가지고 Map에 추가.
        chapterIdsFromBook.forEach(chapterId -> {
            if(!chapterIdSet.contains(chapterId)) {
                pageSummariesGroupedByChapter.put(chapterId, null);
            }
        });
        
        
        // chapterId를 기준으로 그룹화된 map을 순회하여 각 ChapterSummary 객체를 생성
        List<ChapterSummary> chapterSummaries = pageSummariesGroupedByChapter.entrySet().stream()
                .map(entry -> {
                    Long chapterId = entry.getKey();
                    List<PageSummary> pageSummariesInChapter = entry.getValue();
                    
                    return new ChapterSummary(
                            book.getChapters().stream().filter( // 해당 chapterId를 가진 Chapter 객체를 찾아온다.
                                    chapter -> Objects.equals(chapter.getId(), chapterId)
                            ).findAny().orElseThrow(
                                    () -> new NoSuchEntityException(Chapter.class)
                            ),
                            pageSummariesInChapter // orderNum 오름차순으로 정렬된 PageSummary 리스트
                    );
                    
                })
                .sorted(Comparator.comparingLong(ChapterSummary::getSortPriority)) // 챕터 sortPriority에 따라 정렬
                .toList();
        
        // Outline 구현체 제작 후 반환
        return Outline.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverImgUrl(book.getCoverImgUrl())
                .status(book.getStatus())
                .chapters(chapterSummaries)
                .preferenceStatistics(preferenceService.getPreferenceStatistics(book))
                .build();
    }
    
    public List<BookSummary> getBookSummariesByProfileId(long profileId) {
        return bookRepository.findAllByAuthorId(profileId).stream().map(BookSummary::new).toList();
    }
    
    public Book repoSaveBook(Book book) {
        return bookRepository.save(book);
    }
    
    public Book repoFindBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new NoSuchEntityException(Book.class)
        );
    }
    
    public Chapter repoFindChapterById(Long id) {
        return chapterRepository.findById(id).orElseThrow(
                () -> new NoSuchEntityException(Chapter.class)
        );
    }
    
    public Page repoFindPageById(Long id) {
        return pageRepository.findById(id).orElseThrow(
                () -> new NoSuchEntityException(Page.class)
        );
    }
    
    public Book repoFindBookWithAuthorById(Long id) {
        return bookRepository.findBookWithAuthorById(id).orElseThrow(
                () -> new NoSuchEntityException(Book.class)
        );
    }
    
    public Book repoFindBookWithAuthorAndChapterById(Long id) {
        return bookRepository.findBookWithAuthorAndChapterById(id).orElseThrow(
                () -> new NoSuchEntityException(Book.class)
        );
    }
    
    public void repoDeleteBook(Book book) {
        this.bookRepository.delete(book);
    }
    
    public Chapter repoSaveChapter(Chapter defaultChapter) {
        return chapterRepository.save(defaultChapter);
    }
    
    public void repoDeleteChapter(Chapter chapterToDelete) {
        this.chapterRepository.delete(chapterToDelete);
    }
    
    public Page repoSavePage(Page newDefaultPage) {
        return pageRepository.save(newDefaultPage);
    }
    
    public void repoDeletePage(Page pageToDelete) {
        this.pageRepository.delete(pageToDelete);
    }
}
