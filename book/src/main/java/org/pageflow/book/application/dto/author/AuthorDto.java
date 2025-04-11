package org.pageflow.book.application.dto.author;

import lombok.Value;
import org.pageflow.book.domain.Author;
import org.pageflow.common.user.UID;


/**
 * @author : sechan
 */
@Value
public class AuthorDto {
  UID id;
  String penname;
  String profileImageUrl;

  public static AuthorDto from(Author author) {
    return new AuthorDto(
      author.getUid(),
      author.getPenname(),
      author.getProfileImageUrl()
    );
  }
}
