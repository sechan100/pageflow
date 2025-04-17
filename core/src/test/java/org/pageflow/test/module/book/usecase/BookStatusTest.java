package org.pageflow.test.module.book.usecase;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.book.application.BookCode;
import org.pageflow.book.application.dto.book.BookDto;
import org.pageflow.book.domain.book.entity.Book;
import org.pageflow.book.domain.toc.Toc;
import org.pageflow.book.domain.toc.entity.TocFolder;
import org.pageflow.book.domain.toc.entity.TocNode;
import org.pageflow.book.persistence.BookPersistencePort;
import org.pageflow.book.persistence.toc.TocRepository;
import org.pageflow.book.usecase.ChangeBookStatusUseCase;
import org.pageflow.book.usecase.EditTocUseCase;
import org.pageflow.book.usecase.cmd.NodeIdentifier;
import org.pageflow.common.user.UID;
import org.pageflow.test.module.book.utils.BookUtils;
import org.pageflow.test.module.book.utils.TocUtils;
import org.pageflow.test.module.user.utils.UserUtils;
import org.pageflow.test.shared.AssertResult;
import org.pageflow.test.shared.PageflowTest;
import org.pageflow.user.dto.UserDto;

import java.util.UUID;

/**
 * @author : sechan
 */
@PageflowTest
@RequiredArgsConstructor
public class BookStatusTest {
  private final UserUtils userUtils;
  private final BookUtils bookUtils;
  private final TocUtils tocUtils;
  private final ChangeBookStatusUseCase changeBookStatusUseCase;
  private final EditTocUseCase editTocUseCase;

  private final BookPersistencePort bookPersistencePort;
  private final TocRepository tocRepository;

  @Test
  @DisplayName("DRAFT -> PUBLISHED 상태 변경 결과와 불가능한 연산 등 테스트")
  void publishTest() {
    UserDto user1 = userUtils.createUser("user1");
    UID uid = user1.getUid();
    BookDto book = bookUtils.createBook(user1, "출판 테스트 도서");
    UUID bookId = book.getId();
    Book bookEn = bookPersistencePort.findById(bookId).get();
    tocUtils.buildTree(book)
      .folder("폴더 1", f ->
        f.folder("폴더 1.1")
      )
      .section("섹션 2")
      .section("섹션 3")
      .folder("폴더 4", f ->
        f.section("섹션 4.1")
      );

    // 모든 노드들이 editable인지 확인
    Toc originalToc = tocRepository.loadEditableToc(bookEn);
    originalToc.forEachNode(n -> Assertions.assertTrue(n.isEditable()));

    // 섹션 4.1의 제목을 변경하여 editable node일 때 수정이 가능한지 확인
    TocNode section4_1 = ((TocFolder) originalToc.getRootFolder().getChildren().get(3)).getChildren().get(0);
    NodeIdentifier section4_1Identifier = new NodeIdentifier(uid, bookId, section4_1.getId());
    editTocUseCase.changeSectionTitle(
      section4_1Identifier,
      "섹션 4.1 수정됨"
    );

    // 책 출판
    changeBookStatusUseCase.publish(uid, bookId);

    // 출판 후 모든 node가 readOnlyToc로 변경되었는지 확인
    Toc publishedToc = tocRepository.loadReadonlyToc(bookEn);
    publishedToc.forEachNode(n -> Assertions.assertFalse(n.isEditable()));

    // 출판 후 섹션 4.1의 제목을 변경하여 readOnly node일 때 수정이 불가능한지 확인
    AssertResult.assertCode(
      () -> editTocUseCase.changeSectionTitle(section4_1Identifier, "섹션 4.1 수정됨2"),
      BookCode.BOOK_ACCESS_DENIED
    );
  }

  @Test
  @DisplayName("PUBLISHED -> START_REVISION 상태 변경 결과와 불가능한 연산 등 테스트")
  void startRevisionTest() {
    UserDto user1 = userUtils.createUser("user1");
    UID uid = user1.getUid();
    BookDto book = bookUtils.createBook(user1, "출판 테스트 도서");
    UUID bookId = book.getId();

    tocUtils.buildTree(book)
      .folder("폴더 1", f ->
        f.folder("폴더 1.1")
      )
      .section("섹션 2")
      .section("섹션 3")
      .folder("폴더 4", f ->
        f.section("섹션 4.1")
      );

    // 책 출판
    changeBookStatusUseCase.publish(uid, bookId);

    // 출판 후 개정 시작
    changeBookStatusUseCase.startRevision(uid, bookId);

    // 출판 후 editableToc가 복제되었는지 확인
    Book bookEntity = bookPersistencePort.findById(bookId).get();
    TocFolder rtRoot = tocRepository.loadReadonlyToc(bookEntity).getRootFolder();
    TocFolder etRoot = tocRepository.loadEditableToc(bookEntity).getRootFolder();
    tocUtils.assertSameHierarchyRecusive(rtRoot, etRoot, (readOnly, editable) -> {
      // 복제된 id는 같으면 안됨.
      Assertions.assertNotEquals(readOnly.getId(), editable.getId());
      Assertions.assertEquals(readOnly.getTitle(), editable.getTitle());
      // 복제된 node는 editable, 기존 노드는 readOnly
      Assertions.assertFalse(readOnly.isEditable());
      Assertions.assertTrue(editable.isEditable());
    });
  }

  @Test
  @DisplayName("START_REVISION -> PUBLISHED 상태 변경 결과와 불가능한 연산 등 테스트")
  void rePublishTest() {
    UserDto user1 = userUtils.createUser("user1");
    UID uid = user1.getUid();
    BookDto book = bookUtils.createBook(user1, "출판 테스트 도서");
    UUID bookId = book.getId();

    tocUtils.buildTree(book)
      .folder("폴더 1", f ->
        f.folder("폴더 1.1")
      )
      .section("섹션 2")
      .section("섹션 3")
      .folder("폴더 4", f ->
        f.section("섹션 4.1")
      );

    // 책 출판
    changeBookStatusUseCase.publish(uid, bookId);
    // 출판 후 개정 시작
    changeBookStatusUseCase.startRevision(uid, bookId);
    // 개정 후 책 출판
    changeBookStatusUseCase.publish(uid, bookId);

    // 개정 후 모든 node가 readOnlyToc로 변경되었는지 확인
    Book bookEntity = bookPersistencePort.findById(bookId).get();
    Toc publishedToc = tocRepository.loadReadonlyToc(bookEntity);
    publishedToc.forEachNode(n -> Assertions.assertFalse(n.isEditable()));

    // editable toc를 조회하면 예외발생
    Assertions.assertThrows(Exception.class, () -> tocRepository.loadEditableToc(bookEntity));
  }

  @Test
  @DisplayName("MERGE_REVISION(START_REVESION -> PUBLISHED) 상태 변경 결과와 불가능한 연산 등 테스트")
  void mergeRevistionTest() {
    UserDto user1 = userUtils.createUser("user1");
    UID uid = user1.getUid();
    BookDto book = bookUtils.createBook(user1, "출판 테스트 도서");
    UUID bookId = book.getId();

    tocUtils.buildTree(book)
      .folder("폴더 1", f ->
        f.folder("폴더 1.1")
      )
      .section("섹션 2")
      .section("섹션 3")
      .folder("폴더 4", f ->
        f.section("섹션 4.1")
      );

    // 책 출판
    changeBookStatusUseCase.publish(uid, bookId);

    // 출판 후 개정 시작
    changeBookStatusUseCase.startRevision(uid, bookId);

    // 개정 후 책 병합
    changeBookStatusUseCase.mergeRevision(uid, bookId);

    // 병합 후 모든 node가 readOnlyToc로 변경되었는지 확인
    Book bookEntity = bookPersistencePort.findById(bookId).get();
    Toc publishedToc = tocRepository.loadReadonlyToc(bookEntity);
    publishedToc.forEachNode(n -> Assertions.assertFalse(n.isEditable()));

    // editable toc를 조회하면 예외발생
    Assertions.assertThrows(Exception.class, () -> tocRepository.loadEditableToc(bookEntity));
  }

  @Test
  @DisplayName("CANCEL_REVISION(START_REVESION -> PUBLISHED) 상태 변경 결과와 불가능한 연산 등 테스트")
  void cancelRevistionTest() {
    UserDto user1 = userUtils.createUser("user1");
    UID uid = user1.getUid();
    BookDto book = bookUtils.createBook(user1, "출판 테스트 도서");
    UUID bookId = book.getId();
    Book bookEntity = bookPersistencePort.findById(bookId).get();

    tocUtils.buildTree(book)
      .folder("폴더 1", f ->
        f.folder("폴더 1.1")
      )
      .section("섹션 2")
      .section("섹션 3")
      .folder("폴더 4", f ->
        f.section("섹션 4.1")
      );

    // 책을 만들고 나서 직후의 toc를 로드
    Toc originalToc = tocRepository.loadEditableToc(bookEntity);

    // 책 출판
    changeBookStatusUseCase.publish(uid, bookId);

    // 출판 후 개정 시작
    changeBookStatusUseCase.startRevision(uid, bookId);

    // 개정 후 책 개정 취소
    changeBookStatusUseCase.cancelRevision(uid, bookId);

    // 병합 후 모든 node가 readOnlyToc로 변경되었는지 확인
    Toc publishedToc = tocRepository.loadReadonlyToc(bookEntity);
    publishedToc.forEachNode(n -> {
      Assertions.assertFalse(n.isEditable());
    });

    // editable toc를 조회하면 예외발생
    Assertions.assertThrows(Exception.class, () -> tocRepository.loadEditableToc(bookEntity));
  }
}
