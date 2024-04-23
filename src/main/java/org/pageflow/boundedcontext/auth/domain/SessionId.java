package org.pageflow.boundedcontext.auth.domain;

import org.pageflow.shared.type.SingleValueWrapper;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public class SessionId extends SingleValueWrapper<TSID> {
    // CONSTRUCTOR
    public SessionId(TSID id){ super(id); }
    // FACTORY METHODS
    public static SessionId from(String id){return new SessionId(TSID.from(id));}
    public static SessionId from(Long id){ return new SessionId(TSID.from(id));}
    public static SessionId from(TSID id){return new SessionId(id);}
    public static SessionId random(){return new SessionId(TSID.Factory.getTsid());}
    // CAST METHODS
    public Long toLong(){return super.value.toLong();}
}
