package org.pageflow.domain.book.service;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.constants.BookFetchType;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.infra.file.service.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final FileService fileService;

    private Specification<Book> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Book> b, CriteriaQuery<?> query, CriteriaBuilder cb) {
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

    
    
    /**
     * @return 새로운 책 객체를 반환한다. 작성이 되지 않은 책과, 하나씩의 기본 챕터와 페이지를 가진다.
     */
    public Book createNewBook(Account author) {
        
        String defaultCoverImgUrl = "https://library.kbu.ac.kr/libeka/fileview/3025aced-3e0a-4266-86ed-a1894eb759b3.JPG";
        
        Book book = Book.builder()
                .title("제목을 입력해주세요")
                .chapters(new ArrayList<>())
                .isPublished(false)
                .coverImgUrl(defaultCoverImgUrl)
                .author(author)
                .build();
        
        Chapter defaultChapter = Chapter.builder()
                .title("제목을 입력해주세요")
                .pages(new ArrayList<>())
                .book(book)
                .build();
        
        Page defaultPage = Page.builder()
                .title("제목을 입력해주세요")
                .content("내용을 입력해주세요")
                .chapter(defaultChapter)
                .build();
        
        defaultChapter.getPages().add(defaultPage); // 챕터에 페이지 추가
        book.getChapters().add(defaultChapter); // 책에 챕터 추가
        
        return save(book); // 위의 컬렉션 추가로 영속전이가 발생, Book, Chapter, Page가 모두 영속되고 영속된 Book이 반환된다.
    }
    
    
    /* ###########################
     * JPA Repository Method Spec
     * ###########################
     */
    
    
    public Book save(Book book) {
        return bookRepository.save(book);
    }
    
    /**
     * @param id 책 아이디
     * @param fetchType 가져올 데이터의 범위를 설정. 예를 들어 CHAPTER로 설정할 경우, chapters 배열까지만 가져온다. chapter들의 pages 배열은 null을 가진다.
     * @return
     */
    public Book findWithScopeById(Long id, BookFetchType fetchType) {
        return switch(fetchType) {
            case CHAPTER -> bookRepository.findScopeChapterById(id);
            case PAGE -> bookRepository.findScopePageById(id);
            default -> bookRepository.findScopeBookById(id);
        };
    }
}
