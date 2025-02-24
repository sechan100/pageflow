package org.pageflow.book.port.in;

import org.pageflow.common.user.UID;

import java.util.UUID;

/**
 * @author : sechan
 */
public interface BookAccessPermitter {
  BookPermission getAuthorPermission(UUID bookId, UID uid);
}
