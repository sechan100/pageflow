package org.pageflow.book.persistence.toc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.domain.toc.entity.TocSection;
import org.pageflow.common.result.Result;
import org.pageflow.common.result.code.CommonCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author : sechan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoadEditableTocNodePort {
  private final TocNodePersistencePort nodePort;
  private final TocFolderPersistencePort folderPort;
  private final TocSectionPersistencePort sectionPort;

  public Result<TocNode> loadEditableNode(Book book, UUID nodeId) {
    Result<TocNode> result = Result.fromOptional(nodePort.findById(nodeId));
    return result.flatMap(node -> {
      if(!node.getBook().equals(book)) {
        return Result.of(CommonCode.DATA_NOT_FOUND);
      }
      if(!node.isEditable()) {
        return Result.of(BookCode.CAN_EDIT_BOOK, "해당 노드는 편집할 수 없습니다.");
      }
      return Result.ok(node);
    });
  }

  public Result<TocSection> loadEditableSection(Book book, UUID sectionId) {
    Result<TocSection> result = Result.fromOptional(sectionPort.findById(sectionId));
    return result.flatMap(node -> {
      if(!node.getBook().equals(book)) {
        return Result.of(CommonCode.DATA_NOT_FOUND);
      }
      if(!node.isEditable()) {
        return Result.of(BookCode.CAN_EDIT_BOOK, "해당 노드는 편집할 수 없습니다.");
      }
      return Result.ok(node);
    });
  }

  public Result<TocFolder> loadEditableFolder(Book book, UUID folderId) {
    Result<TocFolder> result = Result.fromOptional(folderPort.findWithChildrenById(folderId));
    return result.flatMap(node -> {
      if(!node.getBook().equals(book)) {
        return Result.of(CommonCode.DATA_NOT_FOUND);
      }
      if(!node.isEditable()) {
        return Result.of(BookCode.CAN_EDIT_BOOK, "해당 노드는 편집할 수 없습니다.");
      }
      return Result.ok(node);
    });
  }

//  public void deleteFolder(Toc toc, UUID folderId) {
//    Collection<TocNode> allNodes = toc.getAllNodes();
//    TocNode folder = toc.get(folderId);
//    Preconditions.checkState(folder.isFolderType());
//    _deleteFolderRecursively(folder, allNodes);
//  }

//  public void deleteAllBookNodes(Book book) {
//    List<TocNode> allNode = tocNodePersistencePort.findAllByBookId(book.getId());
//    for(TocNode node : allNode) {
//      tocNodePersistencePort.delete(node);
//    }
//  }

//  public void deleteSection(TocNode section) {
//    Preconditions.checkState(section.isSectionType());
//    tocNodePersistencePort.delete(section);
//  }

//  private void _deleteFolderRecursively(TocNode folder, Collection<TocNode> allNodes) {
//    Preconditions.checkState(folder.isFolderType());
//    List<TocNode> children = allNodes.stream()
//      .filter(node -> {
//        if(node.isRootFolder()) return false;
//        return node.getParentNodeOrNull().equals(folder);
//      })
//      .toList();
//
//    for(TocNode child : children) {
//      if(child.isParentableNode()) {
//        _deleteFolderRecursively(child, allNodes);
//      }
//      tocNodePersistencePort.delete(child);
//    }
//    tocNodePersistencePort.delete(folder);
//  }

}
