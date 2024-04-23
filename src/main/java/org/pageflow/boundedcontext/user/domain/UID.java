package org.pageflow.boundedcontext.user.domain;

import org.pageflow.shared.type.SingleValueWrapper;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public final class UID extends SingleValueWrapper<TSID> {

    // CONUIDSTRUCTOR
    public UID(TSID id) {
        super(id);
    }
    // FACTORY METHODS
    public static UID from(String id){return new UID(TSID.from(id));}
    public static UID from(Long id){ return new UID(TSID.from(id));}
    public static UID from(TSID id){return new UID(id);}
    public static UID random(){return new UID(TSID.Factory.getTsid());}

    // CAST
    public Long toLong(){return super.value.toLong();}
}
