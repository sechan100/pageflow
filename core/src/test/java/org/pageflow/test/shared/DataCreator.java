package org.pageflow.test.shared;

import lombok.RequiredArgsConstructor;
import org.pageflow.book.domain.BookTitle;
import org.pageflow.book.dto.BookDto;
import org.pageflow.book.port.in.BookUseCase;
import org.pageflow.common.user.RoleType;
import org.pageflow.user.dto.UserDto;
import org.pageflow.user.port.in.SignupCmd;
import org.pageflow.user.port.in.SignupUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : sechan
 */
@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@RequiredArgsConstructor
public class DataCreator {
  private final SignupUseCase signupUseCase;
  private final BookUseCase bookUseCase;

  public UserDto createUser(String username) {
    SignupCmd cmd = SignupCmd.nativeSignup(
      username,
      username,
      username + "@pageflow.org",
      username,
      RoleType.ROLE_USER
    ).getSuccessData();
    return signupUseCase.signup(cmd);
  }

  public BookDto createBook(UserDto user, String title) {
    return bookUseCase.createBook(user.getUid(), BookTitle.of(title), null);
  }
}
