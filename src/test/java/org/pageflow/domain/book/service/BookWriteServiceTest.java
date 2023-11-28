package org.pageflow.domain.book.service;

import org.junit.jupiter.api.Test;
import org.pageflow.domain.user.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    void createBlankBook() {
        for(int i = 0; i < 20; i++) {
            bookWriteService.createBlankBook(profileRepository.findById(1L).orElseThrow());
        }
    }
}