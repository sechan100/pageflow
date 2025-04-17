package org.pageflow.book.persistence.toc;

import jakarta.persistence.EntityManager;
import org.pageflow.book.domain.toc.entity.TocFolder;


/**
 * {@link TocFolder}로 모든 자식 {@link org.pageflow.book.domain.toc.entity.TocNode}들을 관리한다.
 * 새로운 Node를 추가할 때에도, OneToMany Collection에 추가하여 commit될 때, PERSIST를 전파하는 방식으로 엔티티를 저장한다.
 * 이 때, 아직 더티체킹이 발생하지 않은 상태에서 추가된 entity를 가져오게 된다면, 데이터를 가져올 수 없는 상황이 발생할 수 있다.
 * 이는 OneToMany의 엘리먼트를 조작하는 연산에 경우에 언제든 발생가능한데, 특히 데이터 추가와 삭제가 그렇다.
 * 때문에 새로운 데이터를 영속전파로 추가하는 경우에는 명시적으로 folder를 {@link EntityManager#merge(Object)} 해주는 작업이 필요할 수 있다.
 * 해당 클래스의 {@link #save(TocFolder)} 메소드는 내부적으로 그러한 작업을 수행한다.
 *
 * @author : sechan
 */
public interface SaveTocFolderPort {
  TocFolder save(TocFolder folder);
}
