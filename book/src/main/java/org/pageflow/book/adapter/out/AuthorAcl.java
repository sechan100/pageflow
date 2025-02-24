package org.pageflow.book.adapter.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.Author;
import org.pageflow.book.port.out.LoadAuthorPort;
import org.pageflow.common.user.UID;
import org.pageflow.user.port.out.entity.ProfilePersistencePort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorAcl implements LoadAuthorPort {
  private final ProfilePersistencePort profilePort;

  @Override
  public Optional<Author> loadAuthor(UID authorId) {
    return profilePort.findById(authorId.getValue()).map(Author::new);
  }

  @Override
  public Author loadAuthorReference(UID authorId) {
    return new Author(profilePort.getReferenceById(authorId.getValue()));
  }
}
