package org.pageflow.shared.data.entity;


import org.pageflow.shared.type.TSID;
import org.springframework.lang.NonNull;

/**
 * @author : sechan
 */
public interface TsidIdentifiable {
    @NonNull TSID getId();
}
