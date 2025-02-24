package org.pageflow.book.port.in;

import lombok.Value;
import org.pageflow.book.domain.Author;
import org.pageflow.book.domain.BookPermissionPolicy;
import org.pageflow.book.domain.entity.Book;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Book에 접근하여 데이터를 쓰거나 읽을 수 있는 권한을 나타내는 토큰
 * @author : sechan
 */
@Value
public class BookPermission {
  UUID bookId;
  List<BookPermissionPolicy> permittedPolicies;

  private BookPermission(UUID bookId, BookPermissionPolicy... permittedPolicies) {
    this.bookId = bookId;
    this.permittedPolicies = Collections.unmodifiableList(Arrays.asList(permittedPolicies));
  }

  /**
   * 작가 권한으로 허가를 발행
   * @param book
   * @param author
   * @return
   */
  public static BookPermission authorPermission(Book book, Author author) {
    if(book.getAuthor().getUid().equals(author.getUid())){
      return new BookPermission(book.getId(), BookPermissionPolicy.FULL);
    } else {
      return deniedPermission(book.getId());
    }
  }

  public static BookPermission deniedPermission(UUID bookId) {
    return new BookPermission(bookId);
  }

  public boolean isFullPermission() {
    if(permittedPolicies.contains(BookPermissionPolicy.FULL)){
      return true;
    }

    for(BookPermissionPolicy policy : BookPermissionPolicy.values()){
      if(policy == BookPermissionPolicy.FULL){
        continue;
      }
      if(!permittedPolicies.contains(policy)){
        return false;
      }
    }
    return true;
  }

  public boolean isPermitted(BookPermissionPolicy policy){
    return permittedPolicies.contains(policy) || isFullPermission();
  }

}
