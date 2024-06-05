package org.pageflow.boundedcontext.book.adapter.out.persistence.jpa;

import io.vavr.Function1;
import io.vavr.Function2;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 클래스 이름 함부로 바꾸지 말 것. jpa dto projection으로 사용중
 * @author : sechan
 */
@Getter
public class NodeProjection {
    private final Long id;
    private final Long parentId;
    private final int ov;
    private final String title;
    private final Class<? extends NodeJpaEntity> type;

    public NodeProjection(Long id, Long parentId, int ov, String title, Class<? extends NodeJpaEntity> type) {
        this.id = id;
        this.parentId = parentId;
        this.ov = ov;
        this.title = title;
        this.type = type;
    }

    // no projection
    @Getter(AccessLevel.NONE)
    private List<NodeProjection> children;

    public void addAccordingToOv(NodeProjection node){
        if(children == null){
            this.children = new LinkedList<>();
        }
        for(int i = 0; i < children.size(); i++){
            if(children.get(i).ov > node.ov){
                children.add(i, node);
                return;
            }
        }
        children.add(node);
    }

    private <N, F extends N, P extends N> N project(Function2<NodeProjection, List<N>, F> folderMapper, Function1<NodeProjection, P> pageMapper){
        if(type == FolderJpaEntity.class){
            List<N> childrenProjection;
            if(children != null){
                childrenProjection = children.stream()
                    .map(c -> c.project(folderMapper, pageMapper))
                    .toList();
            } else {
                childrenProjection = null;
            }
            return folderMapper.apply(this, childrenProjection);
        } else {
            return pageMapper.apply(this);
        }
    }

    public static class Root {
        private final List<NodeProjection> children;

        public Root(List<NodeProjection> children) {
            this.children = Collections.unmodifiableList(children);
        }

        public <N, F extends N, P extends N> List<N> projectTree(ProjectionStrategy<N, F, P> plan){
            return children.stream()
                .map(c -> c.project(plan.folderMapper, plan.pageMapper))
                .toList();
        }
    }

    public static class ProjectionStrategy<N, F extends N, P extends N> {
        private final Function2<NodeProjection, List<N>, F> folderMapper;
        private final Function1<NodeProjection, P> pageMapper;

        public ProjectionStrategy(Function2<NodeProjection, List<N>, F> folderMapper, Function1<NodeProjection, P> pageMapper) {
            this.folderMapper = folderMapper;
            this.pageMapper = pageMapper;
        }
    }
}
