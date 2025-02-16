//package org.pageflow.core.dev;
//
//import org.pageflow.boundedcontext.book.domain.toc.FolderCreateCmd;
//import org.pageflow.boundedcontext.book.domain.toc.SectionCreateCmd;
//import org.pageflow.boundedcontext.book.dto.TocDto;
//import org.pageflow.boundedcontext.book.port.out.TocPersistencePort;
//import org.pageflow.boundedcontext.book.shared.TocNodeType;
//import org.pageflow.shared.type.TSID;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.function.Supplier;
//
///**
// * @author : sechan
// */
//@Profile("dev")
//@Component
//public class BookCreator {
//  private final TocPersistencePort tocPersistencePort;
//  private final BookUseCase bookUseCase;
//  private final TocUseCase tocUseCase;
//  private final Random random;
//  private final int count;
//
//
//  public BookCreator(
//    @Value("${pageflow.dev.data.random-seed}") int seed,
//    @Value("${pageflow.dev.data.book.count}") int count,
//    TocPersistencePort tocPersistencePort,
//    BookUseCase bookUseCase,
//    TocUseCase tocUseCase
//  ) {
//    this.random = new Random(seed);
//    this.count = count;
//    this.tocPersistencePort = tocPersistencePort;
//    this.bookUseCase = bookUseCase;
//    this.tocUseCase = tocUseCase;
//  }
//
//  @Transactional
//  public void create(Set<TSID> userIds) {
//    Set<TSID> bookIds = createBooks(userIds);
//    for(TSID bookId : bookIds){
//      NodeId rootId = tocPersistencePort.getRootNodeId(BookId.from(bookId));
//      fillParentRecursively(bookId, rootId.getValue(), 3);
//    }
//  }
//
//  /**
//   * @param depth depth만큼 재귀적으로 내려간다. 상위층의 부모노드는 folder로 채울 확률이 높아진다.
//   */
//  private void fillParentRecursively(TSID bookId, TSID parentNodeId, int depth) {
//    if(depth==0){
//      return;
//    }
//    List<TocDto.Node> children = fillParent(
//      bookId,
//      parentNodeId,
//      depth * 25 // depth가 깊어질수록 폴더 확률을 낮춘다.
//    );
//    for(TocDto.Node child : children){
//      if(child.getType()==TocNodeType.FOLDER){
//        fillParentRecursively(bookId, child.getId(), depth - 1);
//      }
//    }
//  }
//
//
//  /**
//   * @param userIds 사용자 ID 목록: 모두 사용하는 것은 아니고 랜덤으로 사용하기에 모든 사용자에게 책이 할당되는 것은 아니다.
//   * @return 생성된 책 ID 집합
//   */
//  private Set<TSID> createBooks(Set<TSID> userIds) {
//    // cmd 생성
//    List<BookCreateCmd> cmds = new ArrayList<>();
//    for(int i = 0; i < count; i++){
//      BookCreateCmd cmd = new BookCreateCmd(
//        userIds.stream().skip(random.nextInt(userIds.size())).findFirst().get(),
//        "테스트 책" + random.nextInt(),
//        "https://test.com/" + random.nextInt()
//      );
//      cmds.add(cmd);
//    }
//    // cmd 사용
//    Set<TSID> bookIds = new HashSet<>();
//    for(BookCreateCmd cmd : cmds){
//      var book = bookUseCase.createBook(cmd);
//      bookIds.add(book.getId());
//    }
//    return bookIds;
//  }
//
//  public TocDto.Node addFolder(TSID bookId, TSID parentNodeId) {
//    FolderCreateCmd cmd = new FolderCreateCmd(bookId, parentNodeId);
//    return tocUseCase.createFolder(cmd);
//  }
//
//  public TocDto.Node addSection(TSID bookId, TSID parentNodeId) {
//    SectionCreateCmd cmd = new SectionCreateCmd(bookId, parentNodeId);
//    return tocUseCase.createSection(cmd);
//  }
//
//  /**
//   * 지정된 parentNode 하위에 percent만큼의 확률로 폴더를, 100-percent만큼의 확률로 섹션을 추가한다.
//   * 해당 행위는 1~10회 중 랜덤으로 반복된다.
//   */
//  public List<TocDto.Node> fillParent(TSID bookId, TSID parentNodeId, int percent) {
//    List<TocDto.Node> rootChildren = oneToTen(() -> {
//      if(doPercent(percent)){
//        return addFolder(bookId, parentNodeId);
//      } else {
//        return addSection(bookId, parentNodeId);
//      }
//    });
//    return rootChildren;
//  }
//
//  private List<TocDto.Node> oneToTen(Supplier<TocDto.Node> consumer) {
//    int loop = random.nextInt(1, 10);
//    List<TocDto.Node> nodes = new ArrayList<>();
//    for(int i = loop; i > 0; i--){
//      nodes.add(consumer.get());
//    }
//    return nodes;
//  }
//
//  private boolean doPercent(int percent) {
//    return random.nextInt(100) < percent;
//  }
//}