package org.pageflow.book.adapter.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.book.port.out.jpa.BookPersistencePort;
import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.User;
import org.pageflow.user.port.out.entity.UserPersistencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthorAcl implements LoadAuthorPort {
  private final BookPersistencePort bookPersistencePort;
  private final UserPersistencePort userPersistencePort;

  @Override
  public Author loadAuthorProxy(UID authorId) {
    User profileProxy = userPersistencePort.getReferenceById(authorId.getValue());
    return new Author(profileProxy);
  }

  @Override
  public AuthorProfileDto loadAuthorProfile(UID authorId) {
    UUID uid = authorId.getValue();
    User user = userPersistencePort.findById(uid).get();
    List<Book> authorBooks = bookPersistencePort.findBooksByAuthorId(uid);
    return new AuthorProfileDto(
      authorId,
      user.getPenname(),
      user.getProfileImageUrl(),
      authorBooks.stream().map(book -> new BookDto(book)).toList(),
      user.getBio()
    );
  }
}
