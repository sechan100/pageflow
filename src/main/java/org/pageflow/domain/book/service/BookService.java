package org.pageflow.domain.book.service;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.model.outline.*;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.book.repository.PageRepository;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.infra.file.service.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final PageRepository pageRepository;
    private final FileService fileService;

    private Specification<Book> search(String kw) {
        return new Specification<>() {
            @Serial
            private static final long serialVersionUID = 1L;
            
            @Override
            public Predicate toPredicate(@NonNull Root<Book> b, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
                // b - 기준을 의미하는 Book 앤티티의 객체(책 제목 검색)

                query.distinct(true); //중복 제거

                Join<Book, Account> u1 = b.join("author", JoinType.LEFT);
                // u1-Book 엔티티와 Account 엔티티를 아우터 조인하여 만든 Account 앤티티 객체.
                // Book 앤티티와 Account 앤티티는 author 속성으로 연결되어 있기 때문에
                // 질문 작성자 검색
                return cb.or(cb.like(b.get("title"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"));
            }
        };
    }

    public org.springframework.data.domain.Page<Book> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Book> spec = search(kw);
        return this.bookRepository.findAll(spec, pageable);
    }
    
    
    @Transactional(readOnly = true)
    public Outline getOutline(Long bookId) {
        
        // Book 엔티티를 author만 fetch join으로 조회.
        Book book = bookRepository.findBookWithAuthorAndChapterById(bookId);
        
        List<PageSummaryWithChapterId> pageSummariesWithChapterId = pageRepository.findAllByChapterIdIn(
                book.getChapters()
                        .stream()
                        .map(BaseEntity::getId)
                        .collect(Collectors.toList())
        );
        
        // 페이지 요약을 챕터 ID 기준으로 그룹화
        Map<Long, List<PageSummaryWithChapterId>> pageSummariesGroupedByChapter = pageSummariesWithChapterId.stream()
                .collect(Collectors.groupingBy(PageSummaryWithChapterId::getChapterId));
        
        // 각 챕터 ID 별로 OutlineChapter 객체 생성
        List<ChapterSummary> chapterSummaries = pageSummariesGroupedByChapter.entrySet().stream()
                .map(entry -> {
                    Long chapterId = entry.getKey();
                    List<PageSummaryWithChapterId> pageSummariesInChapter = entry.getValue();
                    
                    // 각 PageSummaryWithChapterId 객체로부터 PageSummary 객체 생성하고 페이지 ID에 따라 정렬
                    List<PageSummary> pageSummaries = pageSummariesInChapter.stream()
                            .map(PageSummary::new)  // PageSummaryWithChapterId -> PageSummary로 변환
                            .sorted(Comparator.comparing(PageSummary::getOrderNum)) // orderNum에 따라 정렬
                            .toList();
                    
                    return new ChapterSummary(
                            book.getChapters().stream().filter( // 해당 chapterId를 가진 Chapter 객체를 찾아온다.
                                    chapter -> Objects.equals(chapter.getId(), chapterId)
                            ).findAny().orElseThrow(),
                            pageSummaries // orderNum 오름차순으로 정렬된 PageSummary 리스트
                    );
                    
                })
                .sorted(Comparator.comparingInt(ChapterSummary::getOrderNum)) // 챕터 orderNum에 따라 정렬
                .toList();
        
        // Outline 구현체 제작 후 반환
        return Outline.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverImgUrl(book.getCoverImgUrl())
                .published(book.isPublished())
                .chapters(chapterSummaries)
                .build();
    }
    
    
    public Book delegateSave(Book book) {
        return bookRepository.save(book);
    }
    
    public Book delegateFindBookWithAuthorById(Long id) {
        return bookRepository.findBookWithAuthorById(id);
    }
    
    public Book delegateFindBookWithAuthorAndChapterById(Long id) {
        return bookRepository.findBookWithAuthorAndChapterById(id);
    }
}
