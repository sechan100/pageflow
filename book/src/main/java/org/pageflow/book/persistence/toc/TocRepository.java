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
public class TocRepository {
  private final TocFolderRepository tocFolderRepository;

  public boolean existsEditableToc(Book book) {
    Optional<TocFolder> rootFolderOpt = tocFolderRepository.findRootFolder(book.getId(), true, TocNodeConfig.ROOT_FOLDER_TITLE);
    return rootFolderOpt.isPresent();
  }

  public boolean existsReadOnlyToc(Book book) {
    Optional<TocFolder> rootFolderOpt = tocFolderRepository.findRootFolder(book.getId(), false, TocNodeConfig.ROOT_FOLDER_TITLE);
    return rootFolderOpt.isPresent();
  }

  public Toc loadEditableToc(Book book) {
    boolean isEditable = true;
    TocFolder rootFolder = tocFolderRepository.findRootFolder(book.getId(), isEditable, TocNodeConfig.ROOT_FOLDER_TITLE).get();
    return new Toc(book, rootFolder);
  }

  public Toc loadReadonlyToc(Book book) {
    boolean isEditable = false;
    TocFolder rootFolder = tocFolderRepository.findRootFolder(book.getId(), isEditable, TocNodeConfig.ROOT_FOLDER_TITLE).get();
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
  public Toc copyFromReadonlyToEditable(Toc sourceToc) {
    Preconditions.checkArgument(sourceToc.isReadOnlyToc());
    return copyToc(sourceToc, true);
  }

  /**
   * sourceToc로부터 toc를 복사하고 저장한다.
   * sourceToc는 editable toc여야하며, 복제된 toc는 editable toc다.
   * <p>
   * 복사된 toc의 node은 동일한 내용으로 그대로 복사된다.(node 자체, 그리고 연관된 엔티티의 id는 달라진다.)
   * 단, {@link TocNode}의 isEditable 필드는 true로 설정된다.
   *
   * @param sourceToc
   * @return
   */
  public Toc copyFromEditableToReadOnly(Toc sourceToc) {
    Preconditions.checkArgument(sourceToc.isEditableToc());
    return copyToc(sourceToc, false);
  }

  public void deleteToc(Toc toc) {
    tocFolderRepository.delete(toc.getRootFolder());
  }

  /**
   * @param source
   * @param copiedTocEditableState true인 경우 source는 readOnly로 강제되고, 결과는 editable toc가 된다.
   */
  private Toc copyToc(Toc source, boolean copiedTocEditableState) {
    boolean sourceTocEditableState = source.isEditableToc();
    Preconditions.checkArgument(sourceTocEditableState != copiedTocEditableState);

    TocNodeCopier copier = new TocNodeCopier(copiedTocEditableState);
    TocFolder originalRootFolder = source.getRootFolder();
    TocFolder copiedRootFolder = copier.copy(originalRootFolder);
    for(TocNode rootChild : originalRootFolder.getChildren()) {
      copyNodeRecursive(rootChild, copiedRootFolder, copier);
    }
    copiedRootFolder = tocFolderRepository.save(copiedRootFolder);
    return new Toc(source.getBook(), copiedRootFolder);
  }

  private void copyNodeRecursive(TocNode target, TocFolder copiedParent, TocNodeCopier copier) {
    TocNodeType type = TocNodeType.from(target);
    TocNode copiedTocNode = switch(type) {
      case FOLDER -> copier.copy((TocFolder) target);
      case SECTION -> copier.copy((TocSection) target);
    };
    copiedParent.addChildLast(copiedTocNode);

    if(target instanceof TocFolder targetAsFolder) {
      for(TocNode child : targetAsFolder.getChildren()) {
        copyNodeRecursive(child, (TocFolder) copiedTocNode, copier);
      }
    }
  }
}
