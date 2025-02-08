package support;


import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.TocFolder;

import java.util.Random;

/**
 * @author : sechan
 */
public class TocCreator {
  private static final int DEEP = 5;
  private static final int MIN_CHILDREN = 2;
  private static final int MAX_CHILDREN = 10;
  private final Random random;
  private final BookId bookId;
  private final UniqueValueContainer<NodeId> idSet;

  public TocCreator(int seed) {
    this.random = new Random(seed);
    TestOnlyTsidFactory factory = new TestOnlyTsidFactory(seed);
    idSet = new UniqueValueContainer<>(
      () -> {
        return NodeId.from(factory.generate());
      }
    );
    bookId = BookId.from(factory.generate());
  }

  public UniqueValueContainer<NodeId> getIdSet() {
    return idSet;
  }

  public TocRoot create() {
    TocRoot root = new TocRoot(bookId, null);
    buildDefault(root, DEEP);
    return root;
  }


  private void buildDefault(TocFolder parent, int depth) {
    if(depth==0){
      return;
    }
    addFolderOrPage(parent, random.nextInt(MIN_CHILDREN, MAX_CHILDREN));
    parent.getChildren().stream()
      .filter(TocFolder.class::isInstance)
      .forEach(p -> {
        buildDefault((TocFolder) p, depth - 1);
      });
  }

  private TocFolder addPages(TocFolder parent, int pageCount) {
    for(int i = 0; i < pageCount; i++){
      TocSection page = new TocSection(idSet.gen());
      parent._addChildLast(page);
    }
    return parent;
  }

  private TocFolder addFolderOrPage(TocFolder parent, int nodeCount) {
    for(int i = 0; i < nodeCount; i++){
      ChildRole child;
      if(random.nextBoolean()){
        child = new TocSection(idSet.gen());
      } else {
        child = new ParentRole(idSet.gen());
      }
      parent._addChildLast(child);
    }
    return parent;
  }


}
