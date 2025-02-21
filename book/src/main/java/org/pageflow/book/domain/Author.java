package org.pageflow.book.domain;

import org.pageflow.common.user.UID;
import org.pageflow.user.domain.entity.Profile;

/**
 * @author : sechan
 */
public class Author {
  private final Profile profile;

  public Author(Profile profile) {
    this.profile = profile;
  }

  public UID getUid() {
    return profile.getUid();
  }

  public String getPenname() {
    return profile.getPenname();
  }

  public String getProfileImageUrl() {
    return profile.getProfileImageUrl();
  }

  public Profile getProfileJpaEntity() {
    return profile;
  }
}
