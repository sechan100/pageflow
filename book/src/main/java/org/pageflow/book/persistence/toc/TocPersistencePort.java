package org.pageflow.book.persistence.toc;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.pageflow.book.domain.toc.constants.TocNodeType;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TocPersistencePort {
  private final TocFolderPersistencePort tocFolderPersistencePort;

  public boolean existsEditableToc(Book book) {
    Optional<TocFolder> rootFolderOpt = tocFolderPersistencePort.findRootFolder(book.getId(), true, TocNodeConfig.ROOT_FOLDER_TITLE);
    return rootFolderOpt.isPresent();
  }

  public boolean existsReadOnlyToc(Book book) {
    Optional<TocFolder> rootFolderOpt = tocFolderPersistencePort.findRootFolder(book.getId(), false, TocNodeConfig.ROOT_FOLDER_TITLE);
    return rootFolderOpt.isPresent();
  }

  public Toc loadEditableToc(Book book) {
    boolean isEditable = true;
    TocFolder rootFolder = tocFolderPersistencePort.findRootFolder(book.getId(), isEditable, TocNodeConfig.ROOT_FOLDER_TITLE).get();
    return new Toc(book, rootFolder);
  }

  public Toc loadReadonlyToc(Book book) {
    boolean isEditable = false;
    TocFolder rootFolder = tocFolderPersistencePort.findRootFolder(book.getId(), isEditable, TocNodeConfig.ROOT_FOLDER_TITLE).get();
    return new Toc(book, rootFolder);
  }

  /**
   * sourceToc로부터 toc를 복사하고 저장한다.
   * sourceToc는 readOnly toc여야하며, 복제된 toc는 editable toc다.
   * <p>
   * 복사된 toc의 node은 동일한 내용으로 그대로 복사된다.(node 자체, 그리고 연관된 엔티티의 id는 달라진다.)
   * 단, {@link TocNode}의 isEditable 필드는 true로 설정된다.
   *
   * @param sourceToc
   * @return
   */
  public Toc copyReadonlyTocToEditableToc(Toc sourceToc) {
    Preconditions.checkArgument(sourceToc.isReadOnlyToc());

    TocFolder originalRootFolder = sourceToc.getRootFolder();
    TocFolder copiedRootFolder = TocFolder.copyFromReadOnlyToEditable(originalRootFolder);
    copiedRootFolder = tocFolderPersistencePort.save(copiedRootFolder);
    for(TocNode rootChild : originalRootFolder.getChildren()) {
      _copyNodeRecursive(rootChild, copiedRootFolder);
    }
    return new Toc(sourceToc.getBook(), copiedRootFolder);
  }

  /**
   * editableToc를 읽기 전용으로 만든다.
   *
   * @param editableToc readOnlyToc라면 에러.
   * @return
   */
  public Toc makeTocReadonly(Toc editableToc) {
    return _makeTocOf(editableToc, false);
  }

  /**
   * readOnlyToc를 편집 가능한 상태로 만든다.
   *
   * @param readOnlyToc editableToc라면 에러.
   * @return
   */
  public Toc makeTocEditable(Toc readOnlyToc) {
    return _makeTocOf(readOnlyToc, true);
  }

  public void deleteToc(Toc toc) {
    tocFolderPersistencePort.delete(toc.getRootFolder());
  }

  private Toc _makeTocOf(Toc toc, boolean isEditable) {
    Preconditions.checkArgument(
      toc.isEditableToc() != isEditable,
      String.format("Toc의 상태가 이미 %s입니다", isEditable ? "editable" : "readOnly")
    );
    toc.forEachNode(n -> n.setEditable(isEditable));
    return toc;
  }

  private void _copyNodeRecursive(TocNode target, TocFolder copiedParent) {
    TocNodeType type = TocNodeType.from(target);
    TocNode copiedTocNode = switch(type) {
      case FOLDER -> TocFolder.copyFromReadOnlyToEditable((TocFolder) target);
      case SECTION -> TocSection.copyFromReadOnlyToEditable((TocSection) target);
    };
    copiedParent.addChildLast(copiedTocNode);

    if(target instanceof TocFolder targetAsFolder) {
      for(TocNode child : targetAsFolder.getChildren()) {
        _copyNodeRecursive(child, (TocFolder) copiedTocNode);
      }
    }
  }
}
