package org.pageflow.domain.book.service;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.DataNotFoundException;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.service.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
// final 필드들 매개변수로 받는 생성자 자동 생성
// spring에서 편의로 생성자가 딱 하나 일때는 @Auto 안 붙여도 자동으로 붙여줌
public class BookService {

    private final BookRepository bookRepository;
//    private final ChapterRepository chapterRepository;
//    private final PageRepository pageRepository;
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

    public Book getBook(Long id) {
        Optional<Book> book = this.bookRepository.findById(id);
        if (book.isPresent()) {
            return book.get();
        } else {
            throw new DataNotFoundException("book not found");
        }
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

        Book savedBook = bookRepository.save(book);

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



    public Book modify(Book book, String title, MultipartFile file, Account author) throws IOException {

        book.setTitle(title);
        book.setAuthor(author);

        FileMetadata bookCoverFileMetadata = fileService.uploadFile(file, book, FileMetadataType.BOOK_COVER);
        String imgUri = fileService.getImgUri(bookCoverFileMetadata);
        book.setCoverImgUrl(imgUri);

        return bookRepository.save(book);
    } // 수정

    public void delete(Book book){
        this.bookRepository.delete(book);
    }
    // 삭제




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
