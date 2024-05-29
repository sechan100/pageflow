package org.pageflow.boundedcontext.book;

import org.pageflow.boundedcontext.book.port.in.BookUseCase;
import org.pageflow.boundedcontext.book.port.in.TocUseCase;
import org.pageflow.boundedcontext.user.UserUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author : sechan
 */
@SpringBootTest
class UseCaseTest {
    @Autowired
    private UserUseCase userUseCase;
    @Autowired
    private BookUseCase bookUseCase;
    @Autowired
    private TocUseCase tocUseCase;
}