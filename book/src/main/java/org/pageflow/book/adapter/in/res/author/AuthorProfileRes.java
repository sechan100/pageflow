package org.pageflow.book.adapter.in.res.author;

import lombok.Value;
import org.pageflow.book.adapter.in.res.book.SimpleBookRes;
import org.pageflow.book.application.dto.author.AuthorProfileDto;
import org.pageflow.common.user.UID;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class AuthorProfileRes {
  UID id;
  String penname;
  String profileImageUrl;
  List<SimpleBookRes> books;
  String bio;

  public AuthorProfileRes(AuthorProfileDto dto) {
    this.id = dto.getId();
    this.penname = dto.getPenname();
    this.profileImageUrl = dto.getProfileImageUrl();
    this.books = dto.getBooks().stream()
      .map(SimpleBookRes::new)
      .toList();
    this.bio = dto.getBio();
  }
}
