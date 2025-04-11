package org.pageflow.book.adapter.in.res;

import lombok.Value;
import org.pageflow.book.application.dto.AuthorDto;
import org.pageflow.common.user.UID;

/**
 * @author : sechan
 */
@Value
public class AuthorRes {
  UID id;
  String penname;
  String profileImageUrl;

  public AuthorRes(AuthorDto dto) {
    this.id = dto.getId();
    this.penname = dto.getPenname();
    this.profileImageUrl = dto.getProfileImageUrl();
  }
}
