package org.pageflow.book.web.res.book;

import lombok.Value;
import org.pageflow.book.application.dto.book.BookmarkDto;

/**
 * @author : sechan
 */
@Value
public class BookmarkRes {
  boolean isBookmarkExist;
  BookmarkDto bookmark;
}
