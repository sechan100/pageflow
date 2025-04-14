package org.pageflow.book.port.out;

import org.pageflow.book.domain.entity.Book;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.Toc;

/**
 * 기본적으로
 *
 * @author : sechan
 */
public interface TocTreePersistencePort {
  boolean existsEditableToc(Book book);

  boolean existsReadOnlyToc(Book book);

  /**
   * sourceToc로부터 toc를 복사하고 저장한다.
   * sourceToc는 readOnly toc여야하며, 복제된 toc는 editable toc다.
   * <p>
   * 복사된 toc의 node은 동일한 내용으로 그대로 복사된다.(node 자체, 그리고 연관된 엔티티의 id는 달라진다.)
   * 단, {@link TocNode#getIsEditable()}은 true로 설정된다.
   *
   * @param sourceToc
   * @return
   */
  Toc copyReadonlyTocToEditableToc(Toc sourceToc);

  /**
   * editableToc를 읽기 전용으로 만든다.
   *
   * @param editableToc readOnlyToc라면 에러.
   * @return
   */
  Toc makeTocReadonly(Toc editableToc);

  /**
   * readOnlyToc를 편집 가능한 상태로 만든다.
   *
   * @param readOnlyToc editableToc라면 에러.
   * @return
   */
  Toc makeTocEditable(Toc readOnlyToc);

  void deleteToc(Toc toc);
}