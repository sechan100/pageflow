package org.pageflow.boundedcontext.book.aggregate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pageflow.boundedcontext.book.command.AddNewFolderCmd;
import org.pageflow.boundedcontext.book.entity.BookEntity;
import org.pageflow.boundedcontext.book.model.Outline;
import org.pageflow.boundedcontext.book.service.BookService;
import org.pageflow.boundedcontext.user.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@SpringBootTest
class BookServiceTest {
    @Autowired
    private BookService bookService;
    @Autowired
    private ProfileRepo profileRepo;

    @BeforeEach
    @Transactional
    @Commit
    public void createBook(){
        BookEntity book = bookService.createNewBook(
            profileRepo.findById(1L).get()
        );
    }

    @Test
    @Commit
    @Transactional
    public void addNewFolder(){
        Outline outline = new Outline(1L);
        outline.addNewFolder("새폴더다");
    }

    @Test
    @Transactional
    @Commit
    public void addNewFolderDirectly(){
        Outline outline = new Outline(1L);
        bookService.addNewFolder(
            new AddNewFolderCmd(outline, "새폴더다")
        );
    }
}
