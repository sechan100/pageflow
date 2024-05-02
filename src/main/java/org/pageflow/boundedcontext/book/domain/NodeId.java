package org.pageflow.boundedcontext.book.domain;

import org.pageflow.shared.type.SingleValueWrapper;
import org.pageflow.shared.type.TSID;

// node id
public final class NodeId extends SingleValueWrapper<TSID> {
    // CONUIDSTRUCTOR
    public NodeId(TSID id) {
        super(id);
    }
    // FACTORY METHODS
    public static NodeId from(String id){return new NodeId(TSID.from(id));}
    public static NodeId from(Long id){ return new NodeId(TSID.from(id));}
    public static NodeId from(TSID id){return new NodeId(id);}
    public static NodeId random(){return new NodeId(TSID.Factory.getTsid());}
    // CAST
    public Long toLong(){return super.value.toLong();}
}