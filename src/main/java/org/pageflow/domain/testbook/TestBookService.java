package org.pageflow.domain.testbook;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.infra.file.constants.FileMetadataType;
import org.pageflow.infra.file.entity.FileMetadata;
import org.pageflow.infra.file.service.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class TestBookService {
    private final TestBookRepository testBookRepository;
    private final FileService fileService;

    private Specification<TestBook> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TestBook> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<TestBook, Account> u1 = q.join("author", JoinType.LEFT);

                return cb.or(cb.like(q.get("title"), "%" + kw + "%"),
                        cb.like(q.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"));
            }
        };
    }
    public Slice<TestBook> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<TestBook> spec = search(kw);
        return this.testBookRepository.findAll(spec, pageable);
    }

//    public void create(String title, MultipartFile file, String content, Account author)  throws IOException {
//      TestBook testBook = TestBook
//              .builder()
//              .title(title)
//              .content(content)
//              .author(author)
//              .build();
//       TestBook saveBook = testBookRepository.save(testBook);
//
//        FileMetadata bookCoverFileMetadata = fileService.uploadFile(file, saveBook, FileMetadataType.TESTBOOK_COVER);
//        String imgUri = fileService.getImgUri(bookCoverFileMetadata);
//        saveBook.setCoverImgUrl(imgUri);
//    }
}
