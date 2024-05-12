package util;

import org.pageflow.boundedcontext.book.domain.BookId;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.Toc;
import org.pageflow.boundedcontext.book.domain.toc.TocFolder;
import org.pageflow.boundedcontext.book.domain.toc.TocNode;
import org.pageflow.boundedcontext.book.domain.toc.TocPage;
import org.pageflow.shared.type.TSID;

import java.util.Random;

/**
 * @author : sechan
 */
public class TocFactory {
    private final Random random;
    private final BookId bookId;
    private final UniqueValueContainer<NodeId> idSet;

    public TocFactory(Random random) {
        this.random = random;
        TSID.Factory factory = TSID.Factory
            .builder()
            .withRandom(random)
            .build();
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

    public Toc create() {
        TocFolder root = new TocFolder(idSet.gen(), 0);
        buildDefault(root, 5);
        return new Toc(bookId, root);
    }



    private void buildDefault(TocFolder f, int depth){
        if(depth == 0){
            return;
        }
        addFolderOrPage(f, random.nextInt(0, 10));
        f.chidrenStream()
            .filter(TocFolder.class::isInstance)
            .forEach(folder -> {
                buildDefault((TocFolder) folder, depth - 1);
            });
    }

    private TocFolder addPages(TocFolder folder, int pageCount) {
        for (int i = 0; i < pageCount; i++) {
            TocPage page = new TocPage(idSet.gen());
            folder.addChildLast(page);
        }
        return folder;
    }

    private TocFolder addFolderOrPage(TocFolder folder, int nodeCount) {
        for (int i = 0; i < nodeCount; i++) {
            TocNode child;
            if(random.nextBoolean()){
                child = new TocPage(idSet.gen());
            } else {
                child = new TocFolder(idSet.gen());
            }
            folder.addChildLast(child);
        }
        return folder;
    }


}
