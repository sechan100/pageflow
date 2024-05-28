package org.pageflow.boundedcontext.book.port.in;

import org.junit.jupiter.api.Test;
import org.pageflow.boundedcontext.auth.shared.RoleType;
import org.pageflow.boundedcontext.book.application.dto.BookDto;
import org.pageflow.boundedcontext.book.application.dto.TocDto;
import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.CoverImageUrl;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.Title;
import org.pageflow.boundedcontext.common.value.UID;
import org.pageflow.boundedcontext.user.application.dto.UserDto;
import org.pageflow.boundedcontext.user.domain.*;
import org.pageflow.boundedcontext.user.port.in.SignupCmd;
import org.pageflow.boundedcontext.user.port.in.UserUseCase;
import org.pageflow.boundedcontext.user.shared.ProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

/**
 * @author : sechan
 */
@SpringBootTest
class TocUseCaseTest {
    @Autowired
    private UserUseCase userUseCase;
    @Autowired
    private BookUseCase bookUseCase;
    @Autowired
    private TocUseCase tocUseCase;


    private BookId createBook() {
        UserDto.User user = userUseCase.signup( new SignupCmd(
            Username.from("test2"),
            Password.encrypt("test1234"),
            Email.from("zosechan100@gmail.com"),
            Penname.from("테스트사용자"),
            RoleType.ROLE_USER,
            ProviderType.NATIVE,
            ProfileImageUrl.from("df")
        ));

        CreateBookCmd cmd = new CreateBookCmd(
            UID.from(user.getUid()),
            Title.from("테스트용 책"),
            CoverImageUrl.from("http://test.com")
        );
        BookDto.Simple dto = bookUseCase.createBook(cmd);
        return new BookId(dto.getId());
    }

    @Test
    @Commit
    void createFolder() {
        BookId bookId = createBook();
        CreateFolderCmd cmd = new CreateFolderCmd(bookId, NodeId.from(0L));
        TocDto.Node node = tocUseCase.createFolder(cmd);
    }
}