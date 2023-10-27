package org.pageflow.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.pageflow.domain.book.entity.Book;
import org.pageflow.domain.book.entity.Chapter;
import org.pageflow.domain.book.entity.Page;
import org.pageflow.domain.book.repository.BookRepository;
import org.pageflow.domain.book.repository.ChapterRepository;
import org.pageflow.domain.book.repository.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
// final 필드들 매개변수로 받는 생성자 자동 생성
// spring에서 편의로 생성자가 딱 하나 일때는 @Auto 안 붙여도 자동으로 붙여줌
public class BookService {

    private final BookRepository bookRepository;
    private ChapterRepository chapterRepository;
    private PageRepository pageRepository;

    public List<Book> getList() {
        return this.bookRepository.findAll();
    }

    public Book create(String title, MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        // src/main/resources/static/bookimg 디렉터리 경로를 지정합니다.
        String directoryPath = "C:\\Users\\SBS\\IdeaProjects\\pageflow\\src\\main\\resources\\static\\bookimg";;
        File uploadDir = new File(directoryPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        File storedFile = new File(uploadDir, storedFileName);

        file.transferTo(storedFile);

        String imgUrl = "/bookimg/" + storedFileName;

        Book book = Book
                .builder()
                .title(title)
                .imgUrl(imgUrl)
                .build();

        return bookRepository.save(book);
    }
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
