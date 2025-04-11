package org.pageflow.book.domain;

import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.User;

/**
 * @author : sechan
 */
public class Author {
  private final User user;

  public Author(User user) {
    this.user = user;
  }

  public UID getUid() {
    return user.getUid();
  }

  public String getPenname() {
    return user.getPenname();
  }

  public String getProfileImageUrl() {
    return user.getProfileImageUrl();
  }

  public User getUserEntity() {
    return user;
  }

}
