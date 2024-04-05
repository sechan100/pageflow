package org.pageflow.boundedcontext.user.model.principal;

import io.hypersistence.tsid.TSID;

/**
 * @author : sechan
 */
public interface PageflowPrincipal {
    TSID getUID();
}
