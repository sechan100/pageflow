package org.pageflow.domain.book.service;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.pageflow.base.entity.BaseEntity;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.model.outline.ChapterSummary;
import org.pageflow.domain.book.model.outline.Outline;
import org.pageflow.domain.book.model.outline.PageSummary;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.book.repository.ChapterRepository;
import org.pageflow.domain.book.repository.PageRepository;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.service.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final PageRepository pageRepository;
    private final ChapterRepository chapterRepository;
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
                            ).findAny().orElseThrow(),
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
                .published(book.isPublished())
                .chapters(chapterSummaries)
                .build();
    }
    
    

    public Book repoSaveBook(Book book) {
        return bookRepository.save(book);
    }
    
    public Book repoFindBookById(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    public Chapter repoFindChapterById(Long id) {
        return chapterRepository.findById(id).orElseThrow();
    }

    public Page repoFindPageById(Long id) {
        return pageRepository.findById(id).orElseThrow();
    }

    public Book repoFindBookWithAuthorById(Long id) {
        return bookRepository.findBookWithAuthorById(id);
    }
    
    public Book repoFindBookWithAuthorAndChapterById(Long id) {
        return bookRepository.findBookWithAuthorAndChapterById(id);
    }

    public Book modify(Book book, String title, MultipartFile file, Account author) throws IOException {

        book.setTitle(title);
        book.setAuthor(author.getProfile());

        FileMetadata bookCoverFileMetadata = fileService.uploadFile(file, book, FileMetadataType.BOOK_COVER);
        String imgUri = fileService.getImgUri(bookCoverFileMetadata);
        book.setCoverImgUrl(imgUri);

        return bookRepository.save(book);
    } // 수정

    public void delete(Book book) {
        this.bookRepository.delete(book);
    }
}
