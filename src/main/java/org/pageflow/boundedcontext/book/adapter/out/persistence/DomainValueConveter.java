package org.pageflow.boundedcontext.book.adapter.out.persistence;

import org.pageflow.boundedcontext.book.domain.NodeId;
import org.springframework.lang.Nullable;

/**
 * 도메인 모델 값을 DB에 실제로 매핑되는 값으로 변환해주는 클래스
 * @author : sechan
 */
public abstract class DomainValueConveter {
    @Nullable
    public static Long convertNodeId(NodeId nodeId){
        if(nodeId.equals(NodeId.ROOT)){
            return null;
        } else {
            return nodeId.toLong();
        }
    }
}
