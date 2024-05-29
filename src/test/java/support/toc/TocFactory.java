package support.toc;


import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.*;
import support.tsid.TestOnlyTsidFactory;
import support.util.UniqueValueContainer;

import java.util.Random;

/**
 * @author : sechan
 */
public class TocFactory {
    private static final int DEEP = 5;
    private static final int MIN_CHILDREN = 2;
    private static final int MAX_CHILDREN = 10;
    private final Random random;
    private final BookId bookId;
    private final UniqueValueContainer<NodeId> idSet;

    public TocFactory(int seed) {
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



    private void buildDefault(TocParent parent, int depth){
        if(depth == 0){
            return;
        }
        addFolderOrPage(parent, random.nextInt(MIN_CHILDREN, MAX_CHILDREN));
        parent.getChildren().stream()
            .filter(TocParent.class::isInstance)
            .forEach(p -> {
                buildDefault((TocParent)p, depth - 1);
            });
    }

    private TocParent addPages(TocParent parent, int pageCount) {
        for (int i = 0; i < pageCount; i++) {
            TocPage page = new TocPage(idSet.gen());
            parent._addChildLast(page);
        }
        return parent;
    }

    private TocParent addFolderOrPage(TocParent parent, int nodeCount) {
        for (int i = 0; i < nodeCount; i++) {
            AbstractChild child;
            if(random.nextBoolean()){
                child = new TocPage(idSet.gen());
            } else {
                child = new TocFolder(idSet.gen());
            }
            parent._addChildLast(child);
        }
        return parent;
    }


}
