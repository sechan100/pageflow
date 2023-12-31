package org.pageflow.domain.book.service;

import org.junit.jupiter.api.Test;
import org.pageflow.domain.book.constants.BookStatus;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@SpringBootTest
@Transactional
class BookWriteServiceTest {
    
    @Autowired
    private BookWriteService bookWriteService;
    @Autowired
    private ProfileRepository profileRepository;
    
    @Test
    @Commit
    void createBlankBook() {
        for(int i = 0; i < 140; i++) {
            bookWriteService.createBlankBook(profileRepository.findById(1L).orElseThrow());
        }
    }
    
    
    
    @Test
    @Commit
    void updateBookStatus() {
        Long bookId = 1L;
        BookStatus status = BookStatus.REVIEW_REQUESTED;
        String rejectReason = "거절 사유";
        
        
        
        if(status == BookStatus.REJECTED){
            bookWriteService.updateBookStatus(bookId, status, rejectReason);
        } else {
            bookWriteService.updateBookStatus(bookId, status);
        }
    }
}