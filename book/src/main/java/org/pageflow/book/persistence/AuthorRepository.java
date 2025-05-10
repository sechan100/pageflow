package org.pageflow.book.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.book.Author;
import org.pageflow.book.domain.book.entity.Book;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorRepository {
  private final BookRepository bookRepository;
  private final UserPersistencePort userPersistencePort;

  public Author loadAuthorProxy(UID authorId) {
    User profileProxy = userPersistencePort.getReferenceById(authorId.getValue());
    return new Author(profileProxy);
  }

  public AuthorProfileDto loadAuthorProfile(UID authorId) {
    UUID uid = authorId.getValue();
    User user = userPersistencePort.findById(uid).get();
    List<Book> authorBooks = bookRepository.findPublishedBooksByAuthorId(uid);
    return new AuthorProfileDto(
      authorId,
      user.getPenname(),
      user.getProfileImageUrl(),
      authorBooks.stream().map(book -> new BookDto(book)).toList(),
      user.getBio()
    );
  }
}
