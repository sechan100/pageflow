package org.pageflow.boundedcontext.book;

import org.junit.jupiter.api.Test;
import org.pageflow.boundedcontext.book.domain.NodeId;
import org.pageflow.boundedcontext.book.domain.toc.ChildableTocNode;
import org.pageflow.boundedcontext.book.domain.toc.TocParent;
import support.TocCreator;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author : sechan
 */
class DomainTest {
    private static final int SEED = 8452;

    private static final int REORDER_CMD_REPEATED = 15;


    @Test
    void sameTocCreateTest(){
        TocRoot root1 = new TocCreator(SEED).create();
        TocRoot root2 = new TocCreator(SEED).create();
        boolean isSameRootTree = root1.isSameTree(root2);
        assertTrue(isSameRootTree, "같은 TocFactory로 생성된 Toc의 root가 다름");
    }

    @Test
    void TocReorderTest() {
        TocCreator factory = new TocCreator(SEED);
        TocRoot root = factory.create();
        TocParent parent = null;
        // 자식을 3개 이상 가진 parent를 찾음
        for(NodeId id : factory.getIdSet().getContainer()){
            if(id.equals(root.getId())) continue;
            ChildableTocNode anyNode = root.findNode(id);
            if(!(anyNode instanceof TocParent p)) continue;
            if(p.getChildren().size() > 2){
                parent = p;
                break;
            }
        }
        assert parent!=null:"자식이 3개 이상인 폴더가 존재하지 않아서 테스트를 진행할 수 없음. 하지만 테스트가 실패한 것은 아닙니다.";
        Random RANDOM = new Random(SEED);
        List<TocChild> childrenCopy = new LinkedList<>(parent.getChildren());
        for(int i=0; i < REORDER_CMD_REPEATED; i++){
            // 복제본 이동
            TocChild target = childrenCopy.remove(RANDOM.nextInt(childrenCopy.size()));
            int dest = RANDOM.nextInt(childrenCopy.size() - 1);
            childrenCopy.add(dest, target);
            // 도메인 로직 reorder
            parent.reorder(dest, target);
        }

        assertEquals(childrenCopy, parent.getChildren(), "reorder 실패");
        boolean isOvAscending = parent.getChildren().stream()
            .map(TocChild::getOv)
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
        TocCreator factory = new TocCreator(SEED);
        TocRoot root = factory.create();
        TocParent parent = null;
        // 자식을 3개 이상 가진 folder를 찾음
        // 자식을 3개 이상 가진 parent를 찾음
        for(NodeId id : factory.getIdSet().getContainer()){
            if(id.equals(root.getId())) continue;
            ChildableTocNode anyNode = root.findNode(id);
            if(!(anyNode instanceof TocParent p)) continue;
            if(p.getChildren().size() > 2){
                parent = p;
                break;
            }
        }
        assert parent!=null: "자식이 3개 이상인 폴더가 존재하지 않아서 테스트를 진행할 수 없음. 하지만 테스트가 실패한 것은 아닙니다.";
        List<TocChild> nodes = parent.getChildren();
        AtomicInteger ov = new AtomicInteger(0);
        nodes.forEach(n -> {
            try {
                Field ovField = ChildRole.class.getDeclaredField("ov");
                ovField.setAccessible(true);
                ovField.set(n, ov.getAndIncrement());
            } catch(NoSuchFieldException | IllegalAccessException e){
                throw new RuntimeException(e);
            }
        });
        parent.reorder(new Random(SEED).nextInt(1, nodes.size() - 1), nodes.stream().findAny().get());
        boolean isOvRebalanced = parent.getChildren().stream()
            .map(TocChild::getOv)
            .reduce((prev, next) -> {
                if(next - prev == ParentRole.OV_OFFSET){
                    return next;
                } else {
                    return 0;
                }
            })
            .orElse(0) != 0;
        System.out.println(parent.getChildren().stream().map(TocChild::getOv).toList());
        assertTrue(isOvRebalanced, "ov가 rebalance되지 않음");
    }

    @Test
    void findNodeTest(){
        TocCreator factory = new TocCreator(SEED);
        TocRoot root = factory.create();
        for(NodeId id: factory.getIdSet().getContainer()){
            ChildableTocNode node = root.findNode(id); // 어차피 못찾으면 예외발생
            assertNotNull(node, "findNode 실패");
        }
    }
}