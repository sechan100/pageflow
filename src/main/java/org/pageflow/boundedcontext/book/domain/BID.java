package org.pageflow.boundedcontext.book.domain;

import org.pageflow.shared.type.SingleValueWrapper;
import org.pageflow.shared.type.TSID;

// book id
public final class BID extends SingleValueWrapper<TSID> {
    // CONUIDSTRUCTOR
    public BID(TSID id) {
        super(id);
    }
    // FACTORY METHODS
    public static BID from(String id){return new BID(TSID.from(id));}
    public static BID from(Long id){ return new BID(TSID.from(id));}
    public static BID from(TSID id){return new BID(id);}
    public static BID random(){return new BID(TSID.Factory.getTsid());}
    // CAST
    public Long toLong(){return super.value.toLong();}
}