package org.pageflow.domain.book.service;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.book.repository.ChapterRepository;
import org.pageflow.domain.book.repository.PageRepository;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.service.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
// final 필드들 매개변수로 받는 생성자 자동 생성
// spring에서 편의로 생성자가 딱 하나 일때는 @Auto 안 붙여도 자동으로 붙여줌
public class BookService {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final PageRepository pageRepository;
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

public Page<Book> getList(int page, String kw) {
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    Specification<Book> spec = search(kw);
    return this.bookRepository.findAll(spec, pageable);
}

    public Optional<Book> getBook(Long id) {
        return this.bookRepository.findById(id);
    }
    public Book create(String title, MultipartFile file, Account author) throws IOException {

        Book book = Book
                .builder()
                .title(title)
                .author(author)
                .build();

        Book savedBook = bookRepository.save(book);

        FileMetadata bookCoverFileMetadata = fileService.uploadFile(file, savedBook, FileMetadataType.BOOK_COVER);
        String imgUri = fileService.getImgUri(bookCoverFileMetadata);
        savedBook.setImgUrl(imgUri);


        return bookRepository.save(savedBook);
    }

    public void vote(Book book, Account siteUser) {
        book.getVoter().add(siteUser);
        this.bookRepository.save(book);
    }

    public void modify(Book book, String title, MultipartFile imgUrl) {
        book.setTitle(title);
        book.setImgUrl(book.getImgUrl());
        book.setModifyDate(LocalDateTime.now());
        this.bookRepository.save(book);
    } // 수정

    // 삭제
    public void delete(Book book){
        this.bookRepository.delete(book);
    }

    // 추천 취소
    public void deletelVote(Book book, Account user) {
        book.getVoter().remove(user);
        this.bookRepository.save(book);
    }

//    public Book getBook(Integer id){
//
//        Optional<Book> book = this.bookRepository.findById(id);
//        if(book.isPresent()){
//            return book.get();
//        } else {
//            throw new DataNotFoundException("book not found");
//        }
//
//    }
//    @Transactional
//    public Book createBookWithChaptersAndPages(Book book, List<Chapter> chapters, List<Page> pages) {
//
//        Book savedBooks = bookRepository.save(book);
//
//        for(Chapter chapter : chapters) {
//            chapter.setBook(savedBooks);
//            Chapter savedChapter = chapterRepository.save(chapter);
//
//            for(Page page : pages) {
//                if(page.getChapter() == chapter) {
//                    page.setChapter(savedChapter);
//                    pageRepository.save(page);
//                }
//            }
//        }
//        return savedBooks;
//    }
}
