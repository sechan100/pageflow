package org.pageflow.boundedcontext.book.domain;

import org.pageflow.shared.type.SingleValueWrapper;
import org.pageflow.shared.type.TSID;

// book id
public final class BookId extends SingleValueWrapper<TSID> {
    // CONUIDSTRUCTOR
    public BookId(TSID id) {
        super(id);
    }
    // FACTORY METHODS
    public static BookId from(String id){return new BookId(TSID.from(id));}
    public static BookId from(Long id){ return new BookId(TSID.from(id));}
    public static BookId from(TSID id){return new BookId(id);}
    public static BookId random(){return new BookId(TSID.Factory.getTsid());}
    // CAST
    public Long toLong(){return super.value.toLong();}
}