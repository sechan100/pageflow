package org.pageflow.boundedcontext.book.domain.toc;

import org.junit.jupiter.api.Test;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.springframework.boot.test.context.SpringBootTest;
import util.TocFactory;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : sechan
 */
@SpringBootTest
class TocTest {
    private static final Random RANDOM = new Random(54859);

    private static final int REORDER_CMD_REPEATED = 30;

    @Test
    void TocReorderTest() {
        TocFactory factory = new TocFactory(RANDOM);
        Toc toc = null;
        TocFolder folder = null;
        while(folder == null){
            toc = factory.create();
            // 자식을 3개 이상 가진 folder를 찾음
            for(NodeId id : factory.getIdSet().getContainer()){
                TocFolder f = toc.getRoot().findParentNode(id);
                if(f.getChildren().size() > 2){
                    folder = f;
                    break;
                }
            }
        }
        List<TocNode> childrenCopy = new LinkedList<>(folder.getChildren());
        for(int i=0; i < REORDER_CMD_REPEATED; i++){
            TocNode target = childrenCopy.remove(RANDOM.nextInt(childrenCopy.size()));
            int dest = RANDOM.nextInt(childrenCopy.size() - 1);
            childrenCopy.add(dest, target);
            toc.reorder(target.getId(), dest);
        }

        assertEquals(childrenCopy, folder.getChildren(), "reorder 실패");
        boolean isOvAscending = folder.getChildren().stream()
            .map(TocNode::getOv)
            .reduce((prev, next) -> {
                if(prev > next){
                    return Integer.MIN_VALUE;
                } else {
                    return next;
                }
            })
            .orElse(0) != Integer.MIN_VALUE;
        assertTrue(isOvAscending, "ov가 오름차순이 아님");
    }

    @Test
    void reblanceTest(){
        TocFactory factory = new TocFactory(RANDOM);
        Toc toc = null;
        TocFolder folder = null;
        while(folder == null){
            toc = factory.create();
            // 자식을 3개 이상 가진 folder를 찾음
            for(NodeId id : factory.getIdSet().getContainer()){
                TocFolder f = toc.getRoot().findParentNode(id);
                if(f.getChildren().size() > 2){
                    folder = f;
                    break;
                }
            }
        }

        List<TocNode> nodes = folder.getChildren();
        AtomicInteger ov = new AtomicInteger(0);
        nodes.forEach(n -> {
            try {
                Field ovField = TocNode.class.getDeclaredField("ov");
                ovField.setAccessible(true);
                ovField.set(n, ov.getAndIncrement());
            } catch(NoSuchFieldException | IllegalAccessException e){
                throw new RuntimeException(e);
            }
        });
        toc.reorder(nodes.stream().findAny().get().getId(), RANDOM.nextInt(nodes.size() - 1));
        boolean isOvRebalanced = folder.getChildren().stream()
            .map(TocNode::getOv)
            .reduce((prev, next) -> {
                if(next - prev == TocFolder.OV_OFFSET){
                    return next;
                } else {
                    return 0;
                }
            })
            .orElse(0) != 0;
        System.out.println(folder.chidrenStream().map(TocNode::getOv).toList());
        assertTrue(isOvRebalanced, "ov가 rebalance되지 않음");
    }

    @Test
    void findNodeTest(){
        TocFactory factory = new TocFactory(RANDOM);
        Toc toc = factory.create();
        TocFolder root = toc.getRoot();
        for(NodeId id: factory.getIdSet().getContainer()){
            TocNode node = root.findNode(id); // 어차피 못찾으면 예외발생
            assertNotNull(node, "findNode 실패");
        }
    }

    @Test
    void findParentNodeTest(){
        TocFactory factory = new TocFactory(RANDOM);
        Toc toc = factory.create();
        TocFolder root = toc.getRoot();
        for(NodeId id: factory.getIdSet().getContainer()){
            // root의 부모는 찾을 수 없음
            if(root.getId().equals(id)){
                continue;
            }
            TocFolder folder = root.findParentNode(id); // 어차피 못찾으면 예외발생
            assertNotNull(folder, "findParentNode 실패");
        }
    }
}