package org.pageflow.test.module.book.utils;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.pageflow.book.application.dto.BookDto;
import org.pageflow.book.application.dto.TocDto;
import org.pageflow.book.domain.entity.TocNode;
import org.pageflow.book.domain.toc.TreeNode;
import org.pageflow.book.port.in.EditTocUseCase;
import org.pageflow.common.user.UID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * @author : sechan
 */
@Component
@Transactional
@RequiredArgsConstructor
public class TocUtils {
  private final EditTocUseCase editTocUseCase;

  public TocTreeBuilderFolder buildTree(BookDto bookDto) {
    TocTreeBuildContext context = new TocTreeBuildContext(
      bookDto.getAuthorId(),
      bookDto.getId(),
      editTocUseCase
    );
    UID uid = bookDto.getAuthorId();
    UUID bookId = bookDto.getId();
    TocDto toc = editTocUseCase.getToc(uid, bookId).getSuccessData();
    TocTreeBuilderFolderImpl rootFolderBuilder = new TocTreeBuilderFolderImpl(context, toc.getRoot().getId());
    return rootFolderBuilder;
  }

  public void assertSameHierarchyRecusive(TreeNode node1, TreeNode node2, BiConsumer<TocNode, TocNode> compareAssert) {
    // 기본 속성 비교: 타입이 다르면 구조가 다름
    Assertions.assertEquals(node1.getType(), node2.getType());
    // 자식 노드 수가 다르면 구조가 다름
    Assertions.assertEquals(node1.getChildren().size(), node2.getChildren().size());
    // 비교
    compareAssert.accept(node1.getTocNode(), node2.getTocNode());
    // 각 자식 노드에 대해 재귀적으로 계층 구조 비교
    for(int i = 0; i < node1.getChildren().size(); i++) {
      assertSameHierarchyRecusive(node1.getChildren().get(i), node2.getChildren().get(i), compareAssert);
    }
  }
}
