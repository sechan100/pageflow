package org.pageflow.test.module.book.utils;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.toc.constants.TocNodeConfig;
import org.pageflow.book.domain.toc.constants.TocNodeType;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.persistence.toc.TocFolderRepository;
import org.pageflow.book.persistence.toc.TocNodeRepository;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * @author : sechan
 */
@Component
@Transactional
@RequiredArgsConstructor
public class TocUtils {
  private final TocFolderRepository tocFolderRepository;
  private final TocNodeRepository tocNodeRepository;
  private final EditTocUseCase editTocUseCase;

  public TocTreeBuilderFolder buildTree(BookDto dto) {
    return buildTree(dto.getAuthorId(), dto.getId());
  }

  public TocTreeBuilderFolder buildTree(UID uid, UUID bookId) {
    TocTreeBuildContext context = new TocTreeBuildContext(
      uid,
      bookId,
      editTocUseCase
    );
    TocFolder rootFolder = tocFolderRepository.findRootFolder(bookId, true, TocNodeConfig.ROOT_FOLDER_TITLE).get();
    TocTreeBuilderFolderImpl rootFolderBuilder = new TocTreeBuilderFolderImpl(context, rootFolder.getId());
    return rootFolderBuilder;
  }

  public void assertSameHierarchyRecusive(TocNode n1, TocNode n2, BiConsumer<TocNode, TocNode> compareAssert) {
    // 기본 속성 비교: 타입이 다르면 구조가 다름
    TocNodeType n1Type = TocNodeType.from(n1);
    TocNodeType n2Type = TocNodeType.from(n2);
    Assertions.assertEquals(n1Type, n2Type);
    // 비교
    compareAssert.accept(n1, n2);
    // 두 노드가 Folder가 아니라면 종료
    if(n1Type == TocNodeType.SECTION) {
      return;
    }
    TocFolder f1 = (TocFolder) n1;
    TocFolder f2 = (TocFolder) n2;

    // 자식 노드 수가 다르면 구조가 다름
    Assertions.assertEquals(f1.getChildren().size(), f2.getChildren().size());
    // 각 자식 노드에 대해 재귀적으로 계층 구조 비교
    for(int i = 0; i < f1.getChildren().size(); i++) {
      assertSameHierarchyRecusive(f1.getChildren().get(i), f2.getChildren().get(i), compareAssert);
    }
  }

  /**
   * 노드의 순서 검증
   */
  public void assertChildrenStructure(UUID parentId, UUID... expectedChildrenIds) {
    tocNodeRepository.flush();
    TocFolder parent = (TocFolder) tocNodeRepository.findById(parentId).get();
    List<TocNode> expectedChildren = new ArrayList<>();
    for(UUID childId : expectedChildrenIds) {
      TocNode byId = tocNodeRepository.findById(childId).get();
      expectedChildren.add(byId);
    }

    List<TocNode> children = parent.getChildren();
    Assertions.assertEquals(expectedChildren.size(), children.size(),
      "ID가 " + parent.getId() + "인 부모 노드의 자식 수가 예상과 다릅니다");

    for(int i = 0; i < expectedChildren.size(); i++) {
      TocNode expectedNode = expectedChildren.get(i);
      TocNode actualNode = children.get(i);
      Assertions.assertEquals(expectedNode, actualNode,
        "인덱스 " + i + "의 자식 노드 ID가 예상과 다릅니다");
    }
  }

  /**
   * 목차 구조의 최대 깊이 검증
   */
  public void assertFolderDepth(UUID folderId, int expectedDepth) {
    tocNodeRepository.flush();
    TocFolder folder = tocFolderRepository.findById(folderId).get();
    int actualDepth = calculateMaxDepthRecursive(folder);
    Assertions.assertEquals(expectedDepth, actualDepth, "목차 구조의 최대 깊이가 예상과 다릅니다");
  }

  /**
   * 노드의 최대 깊이 계산
   */
  private int calculateMaxDepthRecursive(TocNode node) {
    if(node == null) {
      return 0;
    }

    int maxDepth = 1; // 현재 노드 자체를 포함

    if(node instanceof TocFolder folder && !folder.getChildren().isEmpty()) {
      int childMaxDepth = 0;

      for(TocNode child : folder.getChildren()) {
        int depth = calculateMaxDepthRecursive(child);
        if(depth > childMaxDepth) {
          childMaxDepth = depth;
        }
      }

      maxDepth += childMaxDepth;
    }

    return maxDepth;
  }
}
